package com.prayagi.netratrikaal

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import java.time.LocalDate

/** "Other" tab. Each tool appears only when it works on this phone. Numerology is plain arithmetic on what you type; nothing is saved or sent. */
@Composable fun TrikaalOther() {
    val gold = Color(0xFFF2C14E)
    var name by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    val d: LocalDate? = remember(date) { try { LocalDate.parse(date.trim()) } catch (e: Exception) { null } }
    Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Palette.night2, contentColor = Color(0xFFFFF4DC))) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("Numerology / अंक ज्योतिष", style = MaterialTheme.typography.titleMedium, color = gold)
            Text("Type a name and a date. The numbers are worked out on this phone and are not saved.", style = MaterialTheme.typography.bodySmall)
            OutlinedTextField(value = name, onValueChange = { name = it.take(60) }, label = { Text("Name", maxLines = 1) }, singleLine = true, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = date, onValueChange = { date = it.take(10) }, label = { Text("Date of birth (YYYY-MM-DD)", maxLines = 1) }, singleLine = true, modifier = Modifier.fillMaxWidth())
            if (date.isNotBlank() && d == null) Text("Unavailable: use the format YYYY-MM-DD, for example 1990-07-15.", style = MaterialTheme.typography.bodySmall)
            if (d != null) {
                val mk = Numerology.mulank(d); val bh = Numerology.bhagyank(d); val lp = Numerology.lifePath(d)
                Text("Mulank / मूलांक: $mk", color = gold); Text(Numerology.meaning(mk), style = MaterialTheme.typography.bodySmall)
                Text("Bhagyank / भाग्यांक: $bh", color = gold); Text(Numerology.meaning(bh), style = MaterialTheme.typography.bodySmall)
                Text("Life Path (Pythagorean): $lp", color = gold); Text(Numerology.meaning(lp), style = MaterialTheme.typography.bodySmall)
            }
            val nn = Numerology.nameNumber(name)
            if (nn != null) { Text("Name number (Pythagorean): $nn", color = gold); Text(Numerology.meaning(nn), style = MaterialTheme.typography.bodySmall) }
            Text("Numerology is a traditional belief system, not a science. Read it as general information and for interest.", style = MaterialTheme.typography.bodySmall)
        }
    }
    Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Palette.night2, contentColor = Color(0xFFFFF4DC))) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("Gemstones / रत्न", style = MaterialTheme.typography.titleMedium, color = gold)
            Text("The traditional planet and gemstone pairs in Vedic astrology.", style = MaterialTheme.typography.bodySmall)
            Gemstones.TABLE.forEach { Text("${it.planet} / ${it.planetHi}: ${it.gem} / ${it.gemHi}") }
            Text("This is a traditional reference list only. It does not say which stone suits you, and it is not advice to buy anything.", style = MaterialTheme.typography.bodySmall)
        }
    }
    Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Palette.night2, contentColor = Color(0xFFFFF4DC))) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("Vastu / वास्तु", style = MaterialTheme.typography.titleMedium, color = gold)
            Text("The directions, their guardian and their element as commonly taught in Vastu Shastra.", style = MaterialTheme.typography.bodySmall)
            Vastu.TABLE.forEach { Text("${it.name} / ${it.nameHi}: ${it.deity}, ${it.element}") }
            Text("A traditional reference only. Books differ on some details. It is not a building, safety or legal check.", style = MaterialTheme.typography.bodySmall)
        }
    }
}
