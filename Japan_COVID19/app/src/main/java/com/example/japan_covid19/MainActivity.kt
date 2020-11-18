package com.example.japan_covid19

import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import org.jsoup.Jsoup
import java.time.LocalDate

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val refreshBtn: Button = findViewById(R.id.refresh_btn)
        refreshBtn.setOnClickListener {
            refresh(refreshBtn)
        }

        refresh(refreshBtn)
    }

    private fun refresh(button: Button) {
        button.text = "更新中"
        button.isEnabled = false
        Thread {
            crawlingData()
        }.start()
    }

    private fun crawlingData() {
        var doc = Jsoup.connect("https://newsdigest.jp/pages/coronavirus/").maxBodySize(0).get()
        var elements = doc.select("div.css-g8dn7z div")

        val japan_confirm = findViewById<TextView>(R.id.japan_confirm)
        japan_confirm.text = insertComma(elements[1].text(), 0)
        val japan_confirm_variation = findViewById<TextView>(R.id.japan_confirm_variation)
        japan_confirm_variation.text = insertComma(elements[2].text().substring(3, elements[2].text().length), 1)
        if (elements[2].text().substring(3, 4) == "+") {
            if (elements[2].text().substring(4, elements[2].text().length).toInt() == 0)
                japan_confirm_variation.setTextColor(Color.parseColor("#2f9d27"))
            else
                japan_confirm_variation.setTextColor(Color.parseColor("#ff0000"))
        }
        else
            japan_confirm_variation.setTextColor(Color.parseColor("#2f9d27"))

        val japan_cur_confirm = findViewById<TextView>(R.id.japan_cur_confirm)
        japan_cur_confirm.text = insertComma(elements[10].text(), 0)
        val japan_cur_confirm_variation = findViewById<TextView>(R.id.japan_cur_confirm_variation)
        japan_cur_confirm_variation.text = insertComma(elements[11].text().substring(3, elements[11].text().length), 1)
        if (elements[11].text().substring(3, 4) == "+") {
            if (elements[11].text().substring(4, elements[11].text().length).toInt() == 0)
                japan_cur_confirm_variation.setTextColor(Color.parseColor("#2f9d27"))
            else
                japan_cur_confirm_variation.setTextColor(Color.parseColor("#ff0000"))
        }
        else
            japan_cur_confirm_variation.setTextColor(Color.parseColor("#2f9d27"))

        val japan_recovered = findViewById<TextView>(R.id.japan_recovered)
        japan_recovered.text = insertComma(elements[4].text(), 0)
        val japan_recovered_variation = findViewById<TextView>(R.id.japan_recovered_variation)
        japan_recovered_variation.text = insertComma(elements[5].text().substring(3, elements[5].text().length), 1)
        if (elements[5].text().substring(3, 4) == "+") {
            if (elements[5].text().substring(4, elements[5].text().length).toInt() == 0)
                japan_recovered_variation.setTextColor(Color.parseColor("#ff0000"))
            else
                japan_recovered_variation.setTextColor(Color.parseColor("#2f9d27"))
        }
        else
            japan_recovered_variation.setTextColor(Color.parseColor("#ff0000"))

        val japan_death = findViewById<TextView>(R.id.japan_death)
        japan_death.text = insertComma(elements[7].text(), 0)
        val japan_death_variation = findViewById<TextView>(R.id.japan_death_variation)
        japan_death_variation.text = insertComma(elements[8].text().substring(3, elements[8].text().length), 1)
        if (elements[8].text().substring(3, 4) == "+") {
            if (elements[8].text().substring(4, elements[8].text().length).toInt() == 0)
                japan_death_variation.setTextColor(Color.parseColor("#2f9d27"))
            else
                japan_death_variation.setTextColor(Color.parseColor("#ff0000"))
        }

        else
            japan_death_variation.setTextColor(Color.parseColor("#2f9d27"))

        elements = doc.select("div.css-kb7nvz")
        val update_time_japan: TextView = findViewById(R.id.update_time_japan)
        update_time_japan.text = elements[1].text()

        val jsonData = doc.select("script[id=__NEXT_DATA__]")[0].html()
        val splited = jsonData.split("infected\":")

        var splited2 = jsonData.split("\"changePrefecture\":[")
        splited2 = splited2[1].split("name")

        var infected_variation = ""
        var dead_variation = ""
        var recovered_variation = ""
        var cur_infected_variation = ""
        var exclusion_variation = "0"

        var infected = ""
        var dead = ""
        var recovered = ""
        var exclusion = "0"
        var cur_infected = ""

        var flag: Int = 0
        var j: Int = 0
        for (i in 1..splited.count()-1) {
            j += 1
            if (flag == 1) {
                j -= 1
                flag = 0
                continue
            }

            if (flag == 2) {
                flag = 0
                break
            }


            val delimiter = LocalDate.now().toString() + "\",\"increment\":"
            val temp = splited2[j].split(delimiter)[1]
            Log.i("crawlingData", splited2[j])
            Log.i("crawlingData", temp)
            infected_variation = temp.split(",")[0]
            dead_variation = temp.split(",\"dead\":")[1]
            dead_variation = dead_variation.split(",")[0]
            recovered_variation = temp.split(",\"recovered\":")[1]
            recovered_variation = recovered_variation.split(",")[0]
            if (recovered_variation.contains("}")) {
                recovered_variation = recovered_variation.split("}")[0]
            }
            else {
                exclusion_variation = temp.split(",\"exclusion\":")[1]
                exclusion_variation = exclusion_variation.split("}")[0]
            }
            cur_infected_variation = (infected_variation.toInt() - recovered_variation.toInt() - dead_variation.toInt() + exclusion_variation.toInt()).toString()
            exclusion_variation = "0"

            infected_variation = "+"+infected_variation
            dead_variation = "+"+dead_variation
            if (recovered_variation.toInt() >= 0) {
                recovered_variation = "+"+recovered_variation
            }
            if (cur_infected_variation.toInt() >= 0) {
                cur_infected_variation = "+"+cur_infected_variation
            }

            infected = splited[i].split(",")[0]
            dead = splited[i].split(",\"dead\":")[1]
            dead = dead.split(",")[0]
            recovered = splited[i].split(",\"recovered\":")[1]
            if (recovered.contains("ダイヤモンド")) {
                flag = 1
            }
            if (recovered.contains("チャーター")) {
                flag = 2
            }
            recovered = recovered.split(",")[0]
            if (recovered.contains("}")) {
                recovered = recovered.split("}")[0]
            }
            else {
                exclusion = splited[i].split(",\"exclusion\":")[1]
                exclusion = exclusion.split("}")[0]
            }
            cur_infected = (infected.toInt() - dead.toInt() - recovered.toInt() + exclusion.toInt()).toString()
            exclusion = "0"

            var confirm: TextView = TextView(this)
            var cur_confirm: TextView = TextView(this)
            var recovered_tv: TextView = TextView(this)
            var death: TextView = TextView(this)

            var confirm_variation = TextView(this)
            var cur_confirm_variation = TextView(this)
            var recovered_variation_tv = TextView(this)
            var death_variation = TextView(this)

            if (i == 1) {
                confirm = findViewById(R.id.hokkaidou_confirm)
                cur_confirm = findViewById(R.id.hokkaidou_cur_confirm)
                recovered_tv = findViewById(R.id.hokkaidou_recovered)
                death = findViewById(R.id.hokkaidou_death)

                confirm_variation = findViewById(R.id.hokkaidou_confirm_variation)
                cur_confirm_variation = findViewById(R.id.hokkaidou_cur_confirm_variation)
                recovered_variation_tv = findViewById(R.id.hokkaidou_recovered_variation)
                death_variation = findViewById(R.id.hokkaidou_death_variation)
            }
            else if (i == 2) {
                confirm = findViewById<TextView>(R.id.aomori_confirm)
                cur_confirm = findViewById<TextView>(R.id.aomori_cur_confirm)
                recovered_tv = findViewById<TextView>(R.id.aomori_recovered)
                death = findViewById<TextView>(R.id.aomori_death)

                confirm_variation = findViewById(R.id.aomori_confirm_variation)
                cur_confirm_variation = findViewById(R.id.aomori_cur_confirm_variation)
                recovered_variation_tv = findViewById(R.id.aomori_recovered_variation)
                death_variation = findViewById(R.id.aomori_death_variation)
            }
            else if (i == 3) {
                confirm = findViewById(R.id.iwate_confirm)
                cur_confirm = findViewById(R.id.iwate_cur_confirm)
                recovered_tv = findViewById(R.id.iwate_recovered)
                death = findViewById(R.id.iwate_death)

                confirm_variation = findViewById(R.id.iwate_confirm_variation)
                cur_confirm_variation = findViewById(R.id.iwate_cur_confirm_variation)
                recovered_variation_tv = findViewById(R.id.iwate_recovered_variation)
                death_variation = findViewById(R.id.iwate_death_variation)
            }
            else if (i == 4) {
                confirm = findViewById(R.id.miyagi_confirm)
                cur_confirm = findViewById(R.id.miyagi_cur_confirm)
                recovered_tv = findViewById(R.id.miyagi_recovered)
                death = findViewById(R.id.miyagi_death)

                confirm_variation = findViewById(R.id.miyagi_confirm_variation)
                cur_confirm_variation = findViewById(R.id.miyagi_cur_confirm_variation)
                recovered_variation_tv = findViewById(R.id.miyagi_recovered_variation)
                death_variation = findViewById(R.id.miyagi_death_variation)
            }
            else if (i == 5) {
                confirm = findViewById(R.id.akita_confirm)
                cur_confirm = findViewById(R.id.akita_cur_confirm)
                recovered_tv = findViewById(R.id.akita_recovered)
                death = findViewById(R.id.akita_death)

                confirm_variation = findViewById(R.id.akita_confirm_variation)
                cur_confirm_variation = findViewById(R.id.akita_cur_confirm_variation)
                recovered_variation_tv = findViewById(R.id.akita_recovered_variation)
                death_variation = findViewById(R.id.akita_death_variation)
            }
            else if (i == 6) {
                confirm = findViewById(R.id.yamagata_confirm)
                cur_confirm = findViewById(R.id.yamagata_cur_confirm)
                recovered_tv = findViewById(R.id.yamagata_recovered)
                death = findViewById(R.id.yamagata_death)

                confirm_variation = findViewById(R.id.yamagata_confirm_variation)
                cur_confirm_variation = findViewById(R.id.yamagata_cur_confirm_variation)
                recovered_variation_tv = findViewById(R.id.yamagata_recovered_variation)
                death_variation = findViewById(R.id.yamagata_death_variation)
            }
            else if (i == 7) {
                confirm = findViewById(R.id.fukusima_confirm)
                cur_confirm = findViewById(R.id.fukusima_cur_confirm)
                recovered_tv = findViewById(R.id.fukusima_recovered)
                death = findViewById(R.id.fukusima_death)

                confirm_variation = findViewById(R.id.fukusima_confirm_variation)
                cur_confirm_variation = findViewById(R.id.fukusima_cur_confirm_variation)
                recovered_variation_tv = findViewById(R.id.fukusima_recovered_variation)
                death_variation = findViewById(R.id.fukusima_death_variation)
            }
            else if (i == 8) {
                confirm = findViewById(R.id.ibaraki_confirm)
                cur_confirm = findViewById(R.id.ibaraki_cur_confirm)
                recovered_tv = findViewById(R.id.ibaraki_recovered)
                death = findViewById(R.id.ibaraki_death)

                confirm_variation = findViewById(R.id.ibaraki_confirm_variation)
                cur_confirm_variation = findViewById(R.id.ibaraki_cur_confirm_variation)
                recovered_variation_tv = findViewById(R.id.ibaraki_recovered_variation)
                death_variation = findViewById(R.id.ibaraki_death_variation)
            }
            else if (i == 9) {
                confirm = findViewById(R.id.tochigi_confirm)
                cur_confirm = findViewById(R.id.tochigi_cur_confirm)
                recovered_tv = findViewById(R.id.tochigi_recovered)
                death = findViewById(R.id.tochigi_death)

                confirm_variation = findViewById(R.id.tochigi_confirm_variation)
                cur_confirm_variation = findViewById(R.id.tochigi_cur_confirm_variation)
                recovered_variation_tv = findViewById(R.id.tochigi_recovered_variation)
                death_variation = findViewById(R.id.tochigi_death_variation)
            }
            else if (i == 10) {
                confirm = findViewById(R.id.gunma_confirm)
                cur_confirm = findViewById(R.id.gunma_cur_confirm)
                recovered_tv = findViewById(R.id.gunma_recovered)
                death = findViewById(R.id.gunma_death)

                confirm_variation = findViewById(R.id.gunma_confirm_variation)
                cur_confirm_variation = findViewById(R.id.gunma_cur_confirm_variation)
                recovered_variation_tv = findViewById(R.id.gunma_recovered_variation)
                death_variation = findViewById(R.id.gunma_death_variation)
            }
            else if (i == 11) {
                confirm = findViewById(R.id.saitama_confirm)
                cur_confirm = findViewById(R.id.saitama_cur_confirm)
                recovered_tv = findViewById(R.id.saitama_recovered)
                death = findViewById(R.id.saitama_death)

                confirm_variation = findViewById(R.id.saitama_confirm_variation)
                cur_confirm_variation = findViewById(R.id.saitama_cur_confirm_variation)
                recovered_variation_tv = findViewById(R.id.saitama_recovered_variation)
                death_variation = findViewById(R.id.saitama_death_variation)
            }
            else if (i == 12) {
                confirm = findViewById(R.id.chiba_confirm)
                cur_confirm = findViewById(R.id.chiba_cur_confirm)
                recovered_tv = findViewById(R.id.chiba_recovered)
                death = findViewById(R.id.chiba_death)

                confirm_variation = findViewById(R.id.chiba_confirm_variation)
                cur_confirm_variation = findViewById(R.id.chiba_cur_confirm_variation)
                recovered_variation_tv = findViewById(R.id.chiba_recovered_variation)
                death_variation = findViewById(R.id.chiba_death_variation)
            }
            else if (i == 13) {
                confirm = findViewById(R.id.toukyou_confirm)
                cur_confirm = findViewById(R.id.toukyou_cur_confirm)
                recovered_tv = findViewById(R.id.toukyou_recovered)
                death = findViewById(R.id.toukyou_death)

                confirm_variation = findViewById(R.id.toukyou_confirm_variation)
                cur_confirm_variation = findViewById(R.id.toukyou_cur_confirm_variation)
                recovered_variation_tv = findViewById(R.id.toukyou_recovered_variation)
                death_variation = findViewById(R.id.toukyou_death_variation)
            }
            else if (i == 14) {
                confirm = findViewById(R.id.kanagawa_confirm)
                cur_confirm = findViewById(R.id.kanagawa_cur_confirm)
                recovered_tv = findViewById(R.id.kanagawa_recovered)
                death = findViewById(R.id.kanagawa_death)

                confirm_variation = findViewById(R.id.kanagawa_confirm_variation)
                cur_confirm_variation = findViewById(R.id.kanagawa_cur_confirm_variation)
                recovered_variation_tv = findViewById(R.id.kanagawa_recovered_variation)
                death_variation = findViewById(R.id.kanagawa_death_variation)
            }
            else if (i == 16) {
                confirm = findViewById(R.id.niigata_confirm)
                cur_confirm = findViewById(R.id.niigata_cur_confirm)
                recovered_tv = findViewById(R.id.niigata_recovered)
                death = findViewById(R.id.niigata_death)

                confirm_variation = findViewById(R.id.niigata_confirm_variation)
                cur_confirm_variation = findViewById(R.id.niigata_cur_confirm_variation)
                recovered_variation_tv = findViewById(R.id.niigata_recovered_variation)
                death_variation = findViewById(R.id.niigata_death_variation)
            }
            else if (i == 17) {
                confirm = findViewById(R.id.toyama_confirm)
                cur_confirm = findViewById(R.id.toyama_cur_confirm)
                recovered_tv = findViewById(R.id.toyama_recovered)
                death = findViewById(R.id.toyama_death)

                confirm_variation = findViewById(R.id.toyama_confirm_variation)
                cur_confirm_variation = findViewById(R.id.toyama_cur_confirm_variation)
                recovered_variation_tv = findViewById(R.id.toyama_recovered_variation)
                death_variation = findViewById(R.id.toyama_death_variation)
            }
            else if (i == 18) {
                confirm = findViewById(R.id.isikawa_confirm)
                cur_confirm = findViewById(R.id.isikawa_cur_confirm)
                recovered_tv = findViewById(R.id.isikawa_recovered)
                death = findViewById(R.id.isikawa_death)

                confirm_variation = findViewById(R.id.isikawa_confirm_variation)
                cur_confirm_variation = findViewById(R.id.isikawa_cur_confirm_variation)
                recovered_variation_tv = findViewById(R.id.isikawa_recovered_variation)
                death_variation = findViewById(R.id.isikawa_death_variation)
            }
            else if (i == 19) {
                confirm = findViewById(R.id.fukui_confirm)
                cur_confirm = findViewById(R.id.fukui_cur_confirm)
                recovered_tv = findViewById(R.id.fukui_recovered)
                death = findViewById(R.id.fukui_death)

                confirm_variation = findViewById(R.id.fukui_confirm_variation)
                cur_confirm_variation = findViewById(R.id.fukui_cur_confirm_variation)
                recovered_variation_tv = findViewById(R.id.fukui_recovered_variation)
                death_variation = findViewById(R.id.fukui_death_variation)
            }
            else if (i == 20) {
                confirm = findViewById(R.id.yamanasi_confirm)
                cur_confirm = findViewById(R.id.yamanasi_cur_confirm)
                recovered_tv = findViewById(R.id.yamanasi_recovered)
                death = findViewById(R.id.yamanasi_death)

                confirm_variation = findViewById(R.id.yamanasi_confirm_variation)
                cur_confirm_variation = findViewById(R.id.yamanasi_cur_confirm_variation)
                recovered_variation_tv = findViewById(R.id.yamanasi_recovered_variation)
                death_variation = findViewById(R.id.yamanasi_death_variation)
            }
            else if (i == 21) {
                confirm = findViewById(R.id.nagano_confirm)
                cur_confirm = findViewById(R.id.nagano_cur_confirm)
                recovered_tv = findViewById(R.id.nagano_recovered)
                death = findViewById(R.id.nagano_death)

                confirm_variation = findViewById(R.id.nagano_confirm_variation)
                cur_confirm_variation = findViewById(R.id.nagano_cur_confirm_variation)
                recovered_variation_tv = findViewById(R.id.nagano_recovered_variation)
                death_variation = findViewById(R.id.nagano_death_variation)
            }
            else if (i == 22) {
                confirm = findViewById(R.id.gifu_confirm)
                cur_confirm = findViewById(R.id.gifu_cur_confirm)
                recovered_tv = findViewById(R.id.gifu_recovered)
                death = findViewById(R.id.gifu_death)

                confirm_variation = findViewById(R.id.gifu_confirm_variation)
                cur_confirm_variation = findViewById(R.id.gifu_cur_confirm_variation)
                recovered_variation_tv = findViewById(R.id.gifu_recovered_variation)
                death_variation = findViewById(R.id.gifu_death_variation)
            }
            else if (i == 23) {
                confirm = findViewById(R.id.sizuoka_confirm)
                cur_confirm = findViewById(R.id.sizuoka_cur_confirm)
                recovered_tv = findViewById(R.id.sizuoka_recovered)
                death = findViewById(R.id.sizuoka_death)

                confirm_variation = findViewById(R.id.sizuoka_confirm_variation)
                cur_confirm_variation = findViewById(R.id.sizuoka_cur_confirm_variation)
                recovered_variation_tv = findViewById(R.id.sizuoka_recovered_variation)
                death_variation = findViewById(R.id.sizuoka_death_variation)
            }
            else if (i == 24) {
                confirm = findViewById(R.id.aichi_confirm)
                cur_confirm = findViewById(R.id.aichi_cur_confirm)
                recovered_tv = findViewById(R.id.aichi_recovered)
                death = findViewById(R.id.aichi_death)

                confirm_variation = findViewById(R.id.aichi_confirm_variation)
                cur_confirm_variation = findViewById(R.id.aichi_cur_confirm_variation)
                recovered_variation_tv = findViewById(R.id.aichi_recovered_variation)
                death_variation = findViewById(R.id.aichi_death_variation)
            }
            else if (i == 25) {
                confirm = findViewById(R.id.mie_confirm)
                cur_confirm = findViewById(R.id.mie_cur_confirm)
                recovered_tv = findViewById(R.id.mie_recovered)
                death = findViewById(R.id.mie_death)

                confirm_variation = findViewById(R.id.mie_confirm_variation)
                cur_confirm_variation = findViewById(R.id.mie_cur_confirm_variation)
                recovered_variation_tv = findViewById(R.id.mie_recovered_variation)
                death_variation = findViewById(R.id.mie_death_variation)
            }
            else if (i == 26) {
                confirm = findViewById(R.id.siga_confirm)
                cur_confirm = findViewById(R.id.siga_cur_confirm)
                recovered_tv = findViewById(R.id.siga_recovered)
                death = findViewById(R.id.siga_death)

                confirm_variation = findViewById(R.id.siga_confirm_variation)
                cur_confirm_variation = findViewById(R.id.siga_cur_confirm_variation)
                recovered_variation_tv = findViewById(R.id.siga_recovered_variation)
                death_variation = findViewById(R.id.siga_death_variation)
            }
            else if (i == 27) {
                confirm = findViewById(R.id.kyouto_confirm)
                cur_confirm = findViewById(R.id.kyouto_cur_confirm)
                recovered_tv = findViewById(R.id.kyouto_recovered)
                death = findViewById(R.id.kyouto_death)

                confirm_variation = findViewById(R.id.kyouto_confirm_variation)
                cur_confirm_variation = findViewById(R.id.kyouto_cur_confirm_variation)
                recovered_variation_tv = findViewById(R.id.kyouto_recovered_variation)
                death_variation = findViewById(R.id.kyouto_death_variation)
            }
            else if (i == 28) {
                confirm = findViewById(R.id.oosaka_confirm)
                cur_confirm = findViewById(R.id.oosaka_cur_confirm)
                recovered_tv = findViewById(R.id.oosaka_recovered)
                death = findViewById(R.id.oosaka_death)

                confirm_variation = findViewById(R.id.oosaka_confirm_variation)
                cur_confirm_variation = findViewById(R.id.oosaka_cur_confirm_variation)
                recovered_variation_tv = findViewById(R.id.oosaka_recovered_variation)
                death_variation = findViewById(R.id.oosaka_death_variation)
            }
            else if (i == 29) {
                confirm = findViewById(R.id.hyougo_confirm)
                cur_confirm = findViewById(R.id.hyougo_cur_confirm)
                recovered_tv = findViewById(R.id.hyougo_recovered)
                death = findViewById(R.id.hyougo_death)

                confirm_variation = findViewById(R.id.hyougo_confirm_variation)
                cur_confirm_variation = findViewById(R.id.hyougo_cur_confirm_variation)
                recovered_variation_tv = findViewById(R.id.hyougo_recovered_variation)
                death_variation = findViewById(R.id.hyougo_death_variation)
            }
            else if (i == 30) {
                confirm = findViewById(R.id.nara_confirm)
                cur_confirm = findViewById(R.id.nara_cur_confirm)
                recovered_tv = findViewById(R.id.nara_recovered)
                death = findViewById(R.id.nara_death)

                confirm_variation = findViewById(R.id.nara_confirm_variation)
                cur_confirm_variation = findViewById(R.id.nara_cur_confirm_variation)
                recovered_variation_tv = findViewById(R.id.nara_recovered_variation)
                death_variation = findViewById(R.id.nara_death_variation)
            }
            else if (i == 31) {
                confirm = findViewById(R.id.wakayama_confirm)
                cur_confirm = findViewById(R.id.wakayama_cur_confirm)
                recovered_tv = findViewById(R.id.wakayama_recovered)
                death = findViewById(R.id.wakayama_death)

                confirm_variation = findViewById(R.id.wakayama_confirm_variation)
                cur_confirm_variation = findViewById(R.id.wakayama_cur_confirm_variation)
                recovered_variation_tv = findViewById(R.id.wakayama_recovered_variation)
                death_variation = findViewById(R.id.wakayama_death_variation)
            }
            else if (i == 32) {
                confirm = findViewById(R.id.tottori_confirm)
                cur_confirm = findViewById(R.id.tottori_cur_confirm)
                recovered_tv = findViewById(R.id.tottori_recovered)
                death = findViewById(R.id.tottori_death)

                confirm_variation = findViewById(R.id.tottori_confirm_variation)
                cur_confirm_variation = findViewById(R.id.tottori_cur_confirm_variation)
                recovered_variation_tv = findViewById(R.id.tottori_recovered_variation)
                death_variation = findViewById(R.id.tottori_death_variation)
            }
            else if (i == 33) {
                confirm = findViewById(R.id.simane_confirm)
                cur_confirm = findViewById(R.id.simane_cur_confirm)
                recovered_tv = findViewById(R.id.simane_recovered)
                death = findViewById(R.id.simane_death)

                confirm_variation = findViewById(R.id.simane_confirm_variation)
                cur_confirm_variation = findViewById(R.id.simane_cur_confirm_variation)
                recovered_variation_tv = findViewById(R.id.simane_recovered_variation)
                death_variation = findViewById(R.id.simane_death_variation)
            }
            else if (i == 34) {
                confirm = findViewById(R.id.okayama_confirm)
                cur_confirm = findViewById(R.id.okayama_cur_confirm)
                recovered_tv = findViewById(R.id.okayama_recovered)
                death = findViewById(R.id.okayama_death)

                confirm_variation = findViewById(R.id.okayama_confirm_variation)
                cur_confirm_variation = findViewById(R.id.okayama_cur_confirm_variation)
                recovered_variation_tv = findViewById(R.id.okayama_recovered_variation)
                death_variation = findViewById(R.id.okayama_death_variation)
            }
            else if (i == 35) {
                confirm = findViewById(R.id.hirosima_confirm)
                cur_confirm = findViewById(R.id.hirosima_cur_confirm)
                recovered_tv = findViewById(R.id.hirosima_recovered)
                death = findViewById(R.id.hirosima_death)

                confirm_variation = findViewById(R.id.hirosima_confirm_variation)
                cur_confirm_variation = findViewById(R.id.hirosima_cur_confirm_variation)
                recovered_variation_tv = findViewById(R.id.hirosima_recovered_variation)
                death_variation = findViewById(R.id.hirosima_death_variation)
            }
            else if (i == 36) {
                confirm = findViewById(R.id.yamaguchi_confirm)
                cur_confirm = findViewById(R.id.yamaguchi_cur_confirm)
                recovered_tv = findViewById(R.id.yamaguchi_recovered)
                death = findViewById(R.id.yamaguchi_death)

                confirm_variation = findViewById(R.id.yamaguchi_confirm_variation)
                cur_confirm_variation = findViewById(R.id.yamaguchi_cur_confirm_variation)
                recovered_variation_tv = findViewById(R.id.yamaguchi_recovered_variation)
                death_variation = findViewById(R.id.yamaguchi_death_variation)
            }
            else if (i == 37) {
                confirm = findViewById(R.id.tokusima_confirm)
                cur_confirm = findViewById(R.id.tokusima_cur_confirm)
                recovered_tv = findViewById(R.id.tokusima_recovered)
                death = findViewById(R.id.tokusima_death)

                confirm_variation = findViewById(R.id.tokusima_confirm_variation)
                cur_confirm_variation = findViewById(R.id.tokusima_cur_confirm_variation)
                recovered_variation_tv = findViewById(R.id.tokusima_recovered_variation)
                death_variation = findViewById(R.id.tokusima_death_variation)
            }
            else if (i == 38) {
                confirm = findViewById(R.id.kagawa_confirm)
                cur_confirm = findViewById(R.id.kagawa_cur_confirm)
                recovered_tv = findViewById(R.id.kagawa_recovered)
                death = findViewById(R.id.kagawa_death)

                confirm_variation = findViewById(R.id.kagawa_confirm_variation)
                cur_confirm_variation = findViewById(R.id.kagawa_cur_confirm_variation)
                recovered_variation_tv = findViewById(R.id.kagawa_recovered_variation)
                death_variation = findViewById(R.id.kagawa_death_variation)
            }
            else if (i == 39) {
                confirm = findViewById(R.id.ehime_confirm)
                cur_confirm = findViewById(R.id.ehime_cur_confirm)
                recovered_tv = findViewById(R.id.ehime_recovered)
                death = findViewById(R.id.ehime_death)

                confirm_variation = findViewById(R.id.ehime_confirm_variation)
                cur_confirm_variation = findViewById(R.id.ehime_cur_confirm_variation)
                recovered_variation_tv = findViewById(R.id.ehime_recovered_variation)
                death_variation = findViewById(R.id.ehime_death_variation)
            }
            else if (i == 40) {
                confirm = findViewById(R.id.kouchi_confirm)
                cur_confirm = findViewById(R.id.kouchi_cur_confirm)
                recovered_tv = findViewById(R.id.kouchi_recovered)
                death = findViewById(R.id.kouchi_death)

                confirm_variation = findViewById(R.id.kouchi_confirm_variation)
                cur_confirm_variation = findViewById(R.id.kouchi_cur_confirm_variation)
                recovered_variation_tv = findViewById(R.id.kouchi_recovered_variation)
                death_variation = findViewById(R.id.kouchi_death_variation)
            }
            else if (i == 41) {
                confirm = findViewById(R.id.fukuoka_confirm)
                cur_confirm = findViewById(R.id.fukuoka_cur_confirm)
                recovered_tv = findViewById(R.id.fukuoka_recovered)
                death = findViewById(R.id.fukuoka_death)

                confirm_variation = findViewById(R.id.fukuoka_confirm_variation)
                cur_confirm_variation = findViewById(R.id.fukuoka_cur_confirm_variation)
                recovered_variation_tv = findViewById(R.id.fukuoka_recovered_variation)
                death_variation = findViewById(R.id.fukuoka_death_variation)
            }
            else if (i == 42) {
                confirm = findViewById(R.id.saga_confirm)
                cur_confirm = findViewById(R.id.saga_cur_confirm)
                recovered_tv = findViewById(R.id.saga_recovered)
                death = findViewById(R.id.saga_death)

                confirm_variation = findViewById(R.id.saga_confirm_variation)
                cur_confirm_variation = findViewById(R.id.saga_cur_confirm_variation)
                recovered_variation_tv = findViewById(R.id.saga_recovered_variation)
                death_variation = findViewById(R.id.saga_death_variation)
            }
            else if (i == 43) {
                confirm = findViewById(R.id.nagasaki_confirm)
                cur_confirm = findViewById(R.id.nagasaki_cur_confirm)
                recovered_tv = findViewById(R.id.nagasaki_recovered)
                death = findViewById(R.id.nagasaki_death)

                confirm_variation = findViewById(R.id.nagasaki_confirm_variation)
                cur_confirm_variation = findViewById(R.id.nagasaki_cur_confirm_variation)
                recovered_variation_tv = findViewById(R.id.nagasaki_recovered_variation)
                death_variation = findViewById(R.id.nagasaki_death_variation)
            }
            else if (i == 45) {
                confirm = findViewById(R.id.kumamoto_confirm)
                cur_confirm = findViewById(R.id.kumamoto_cur_confirm)
                recovered_tv = findViewById(R.id.kumamoto_recovered)
                death = findViewById(R.id.kumamoto_death)

                confirm_variation = findViewById(R.id.kumamoto_confirm_variation)
                cur_confirm_variation = findViewById(R.id.kumamoto_cur_confirm_variation)
                recovered_variation_tv = findViewById(R.id.kumamoto_recovered_variation)
                death_variation = findViewById(R.id.kumamoto_death_variation)
            }
            else if (i == 46) {
                confirm = findViewById(R.id.ooita_confirm)
                cur_confirm = findViewById(R.id.ooita_cur_confirm)
                recovered_tv = findViewById(R.id.ooita_recovered)
                death = findViewById(R.id.ooita_death)

                confirm_variation = findViewById(R.id.ooita_confirm_variation)
                cur_confirm_variation = findViewById(R.id.ooita_cur_confirm_variation)
                recovered_variation_tv = findViewById(R.id.ooita_recovered_variation)
                death_variation = findViewById(R.id.ooita_death_variation)
            }
            else if (i == 47) {
                confirm = findViewById(R.id.miyazaki_confirm)
                cur_confirm = findViewById(R.id.miyazaki_cur_confirm)
                recovered_tv = findViewById(R.id.miyazaki_recovered)
                death = findViewById(R.id.miyazaki_death)

                confirm_variation = findViewById(R.id.miyazaki_confirm_variation)
                cur_confirm_variation = findViewById(R.id.miyazaki_cur_confirm_variation)
                recovered_variation_tv = findViewById(R.id.miyazaki_recovered_variation)
                death_variation = findViewById(R.id.miyazaki_death_variation)
            }
            else if (i == 48) {
                confirm = findViewById(R.id.kagosima_confirm)
                cur_confirm = findViewById(R.id.kagosima_cur_confirm)
                recovered_tv = findViewById(R.id.kagosima_recovered)
                death = findViewById(R.id.kagosima_death)

                confirm_variation = findViewById(R.id.kagosima_confirm_variation)
                cur_confirm_variation = findViewById(R.id.kagosima_cur_confirm_variation)
                recovered_variation_tv = findViewById(R.id.kagosima_recovered_variation)
                death_variation = findViewById(R.id.kagosima_death_variation)
            }
            else if (i == 49) {
                confirm = findViewById(R.id.okinawa_confirm)
                cur_confirm = findViewById(R.id.okinawa_cur_confirm)
                recovered_tv = findViewById(R.id.okinawa_recovered)
                death = findViewById(R.id.okinawa_death)

                confirm_variation = findViewById(R.id.okinawa_confirm_variation)
                cur_confirm_variation = findViewById(R.id.okinawa_cur_confirm_variation)
                recovered_variation_tv = findViewById(R.id.okinawa_recovered_variation)
                death_variation = findViewById(R.id.okinawa_death_variation)
            }

            confirm.text = insertComma(infected, 0)

            confirm_variation.text = "(" + insertComma(infected_variation, 1) + ")"
            if (infected_variation.toInt() > 0)
                confirm_variation.setTextColor(Color.parseColor("#ff0000"))
            else
                confirm_variation.setTextColor(Color.parseColor("#2f9d27"))

            cur_confirm.text = insertComma(cur_infected, 0)

            cur_confirm_variation.text = "(" + insertComma(cur_infected_variation, 1) + ")"
            if (cur_infected_variation.toInt() > 0)
                cur_confirm_variation.setTextColor(Color.parseColor("#ff0000"))
            else
                cur_confirm_variation.setTextColor(Color.parseColor("#2f9d27"))

            recovered_tv.text = insertComma(recovered, 0)

            recovered_variation_tv.text = "(" + insertComma(recovered_variation, 1) + ")"
            if (recovered_variation.toInt() <= 0)
                recovered_variation_tv.setTextColor(Color.parseColor("#ff0000"))
            else
                recovered_variation_tv.setTextColor(Color.parseColor("#2f9d27"))

            death.text = insertComma(dead, 0)

            death_variation.text = "(" + insertComma(dead_variation, 1) + ")"
            if (dead_variation.toInt() > 0)
                death_variation.setTextColor(Color.parseColor("#ff0000"))
            else
                death_variation.setTextColor(Color.parseColor("#2f9d27"))
        }

        val refreshBtn: Button = findViewById(R.id.refresh_btn)
        runOnUiThread {
            refreshBtn.text = "更新する"
            refreshBtn.isEnabled = true
        }
    }



    private fun insertComma(num_str: String, offset: Int): String {
        val digits = num_str.length-offset

        if (digits <= 3) {
            return num_str
        }
        else {
            val quotient = (digits/3)-1
            val remainder = digits%3

            var new_str = ""
            if (remainder != 0) {
                new_str = num_str.substring(0, offset+remainder)+","
            }
            for (i in 0..quotient) {
                val index = offset + remainder + (i*3)
                new_str += num_str.substring(index, index+3)
                if (i < quotient) {
                    new_str += ","
                }
            }

            return new_str
        }
    }
}