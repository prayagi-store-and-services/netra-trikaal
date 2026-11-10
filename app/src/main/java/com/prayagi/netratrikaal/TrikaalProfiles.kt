package com.prayagi.netratrikaal

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import org.json.JSONArray
import org.json.JSONObject
import javax.crypto.SecretKey

/** Device-private profiles. Never submitted to Eco stats, crash messages, or AI. */
data class TrikaalProfile(val name:String, val date:String, val time:String, val zone:String,
    val latitude:String, val longitude:String, val place:String, val offset:String)

/**
 * Birth profile storage. Profiles are sealed with a device-only Android Keystore key
 * (pref key "profiles_enc"); a pre-encryption plaintext "profiles" value migrates on first read.
 * When the keystore is unusable, writes fall back to plaintext with a warning (same honesty rule
 * as before: no silent failure, no data leaves the phone either way).
 */
object TrikaalProfiles {
    private const val PREFS="trikaal_local_profiles"
    private const val KEY_LEGACY="profiles"
    private const val KEY_SEALED="profiles_enc"
    private const val TAG="TrikaalProfiles"
    const val MAX_PROFILES=5
    fun key(p:TrikaalProfile)="${p.name}|${p.date}|${p.time}|${p.latitude}|${p.longitude}"
    fun defaultKey(context:Context):String=context.getSharedPreferences(PREFS,Context.MODE_PRIVATE).getString("default","") ?: ""
    fun setDefault(context:Context,p:TrikaalProfile):Boolean=context.getSharedPreferences(PREFS,Context.MODE_PRIVATE).edit().putString("default",key(p)).commit()
    fun defaultProfile(context:Context):TrikaalProfile?=read(context).firstOrNull{key(it)==defaultKey(context)}
    fun read(context:Context):List<TrikaalProfile> =
        readFrom(context.getSharedPreferences(PREFS,Context.MODE_PRIVATE), TrikaalCrypto.keystoreKey())
    fun save(context:Context, p:TrikaalProfile):Boolean {
        val existing=read(context)
        if(p !in existing && existing.size>=MAX_PROFILES) return false
        return writeTo(context.getSharedPreferences(PREFS,Context.MODE_PRIVATE), existing.filterNot{it==p}+p, TrikaalCrypto.keystoreKey())
    }
    fun delete(context:Context,p:TrikaalProfile):Boolean =
        writeTo(context.getSharedPreferences(PREFS,Context.MODE_PRIVATE), read(context).filterNot{it==p}, TrikaalCrypto.keystoreKey())

    internal fun readFrom(sp:SharedPreferences, key:SecretKey?):List<TrikaalProfile> {
        val sealed=sp.getString(KEY_SEALED,null)
        if (sealed!=null) {
            if (key==null) { Log.w(TAG,"keystore unavailable; sealed profiles unreadable this run"); return emptyList() }
            return try { parse(TrikaalCrypto.open(key,sealed)) } catch(e:Exception) { Log.w(TAG,"sealed profiles failed integrity check",e); emptyList() }
        }
        val legacy=sp.getString(KEY_LEGACY,null) ?: return emptyList()
        val parsed=try { parse(legacy) } catch(e:Exception) { Log.w(TAG,"legacy profiles unreadable",e); return emptyList() }
        // Migrate to sealed storage now that we had to read the plaintext anyway.
        if (key!=null && parsed.isNotEmpty()) {
            try {
                if (sp.edit().putString(KEY_SEALED,TrikaalCrypto.seal(key,toJson(parsed))).remove(KEY_LEGACY).commit())
                    Log.i(TAG,"profiles migrated to encrypted storage")
            } catch(e:Exception) { Log.w(TAG,"migration to encrypted storage failed; plaintext kept",e) }
        }
        return parsed
    }

    internal fun writeTo(sp:SharedPreferences, profiles:List<TrikaalProfile>, key:SecretKey?):Boolean {
        val json=toJson(profiles)
        if (key!=null) {
            return try { sp.edit().putString(KEY_SEALED,TrikaalCrypto.seal(key,json)).remove(KEY_LEGACY).commit() }
            catch(e:Exception) { Log.w(TAG,"encryption failed; saving without encryption",e); sp.edit().putString(KEY_LEGACY,json).commit() }
        }
        Log.w(TAG,"keystore unavailable; saving profiles without encryption")
        return sp.edit().putString(KEY_LEGACY,json).commit()
    }

    private fun parse(json:String):List<TrikaalProfile> {
        val a=JSONArray(json)
        return (0 until a.length()).map { i -> val o=a.getJSONObject(i); TrikaalProfile(o.optString("name"),o.optString("date"),o.optString("time"),o.optString("zone"),o.optString("lat"),o.optString("lon"),o.optString("place"),o.optString("offset")) }
    }
    private fun toJson(profiles:List<TrikaalProfile>):String {
        val a=JSONArray(); profiles.forEach { a.put(JSONObject().put("name",it.name).put("date",it.date).put("time",it.time).put("zone",it.zone).put("lat",it.latitude).put("lon",it.longitude).put("place",it.place).put("offset",it.offset)) }
        return a.toString()
    }
}
