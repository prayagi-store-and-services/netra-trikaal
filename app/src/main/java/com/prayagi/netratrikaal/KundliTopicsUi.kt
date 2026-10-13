package com.prayagi.netratrikaal

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prayagi.trikaal.Chart
import com.prayagi.trikaal.vimshottariDasha
import java.time.Instant

internal val TOPICS = listOf(1 to "पूरी कुंडली (12 भाव)", 2 to "विवाह एवं संबंध", 3 to "करियर एवं व्यवसाय", 4 to "वर्तमान दशा", 5 to "योग और दोष", 6 to "तकनीकी विवरण")

/**
 * Topic switches: nothing is shown until the user taps a topic, and only one topic is open at a time.
 * [topic] 0 means none is open (just the overview above). Every section is built from the calculated chart.
 */
@Composable
internal fun KundliTopicsPanel(c: Chart, now: Instant, topic: Int, onTopic: (Int) -> Unit, vedic: Boolean, onVedic: (Boolean) -> Unit) {
    val gold = Color(0xFFF2C14E)
    Text("एक विषय चुनें", color = gold, fontWeight = FontWeight.Bold, fontSize = 15.sp, maxLines = 1)
    Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        TOPICS.forEach { (id, label) ->
            val on = topic == id
            if (on) Button(onClick = { onTopic(0) }, colors = ButtonDefaults.buttonColors(containerColor = gold, contentColor = Color(0xFF160B33))) { Text(label, maxLines = 1) }
            else OutlinedButton(onClick = { onTopic(id) }, border = BorderStroke(1.dp, gold)) { Text(label, maxLines = 1, color = gold) }
        }
    }
    if (topic == 1) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            if (!vedic) Button(onClick = {}) { Text("सरल हिंदी", maxLines = 1) } else OutlinedButton(onClick = { onVedic(false) }) { Text("सरल हिंदी", maxLines = 1) }
            if (vedic) Button(onClick = {}) { Text("वैदिक शब्द", maxLines = 1) } else OutlinedButton(onClick = { onVedic(true) }) { Text("वैदिक शब्द", maxLines = 1) }
        }
    }
    val sections = remember(c, now, topic, vedic) {
        when (topic) {
            1 -> houseSections(c, vedic)
            2 -> vivahSections(c)
            3 -> careerSections(c)
            4 -> dashaSections(c, vimshottariDasha(c), now)
            5 -> yogSections(c)
            else -> emptyList()
        }
    }
    sections.forEach { s ->
        Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Palette.night2, contentColor = Color(0xFFFFF4DC))) {
            Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(s.title, color = gold, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                s.lines.forEach { Text(it, fontSize = 14.sp, lineHeight = 21.sp) }
            }
        }
    }
    if (sections.isNotEmpty()) Text("यह पारंपरिक वैदिक विचार है, वैज्ञानिक रूप से सिद्ध भविष्यवाणी नहीं। सभी मान आपके फ़ोन पर आपकी कुंडली से निकाले गए हैं।", fontSize = 12.sp, color = Color(0xFFD8CFEA))
}
