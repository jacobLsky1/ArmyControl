package com.jacoblip.andriod.armycontrol.utilities

import android.content.Context
import android.widget.Toast
import com.jacoblip.andriod.armycontrol.data.models.Soldier
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.io.InputStream

class ExcelUtil {
    companion object{
        fun getSoldiersFromExcel(inputStream: InputStream,context: Context):List<Soldier>{
            var excelFile = WorkbookFactory.create(inputStream)
            val sheet = excelFile.getSheetAt(0)
            Toast.makeText(context, "הקובץ התקבל", Toast.LENGTH_SHORT).show()
            var validForm = true
            try {

                val soldiers:ArrayList<Soldier> = arrayListOf()
                var rowSize = sheet.physicalNumberOfRows
                for(num in 1 until rowSize){
                    val row = sheet.getRow(num)
                    var id  = row.getCell(0).toString()
                    if(id.contains('.')){
                        id = id.substring(0,id.indexOf('.'))
                    }
                    var positionMap = ""
                    val firstName  = row.getCell(1).toString()
                    val lastName  = row.getCell(2).toString()
                    var jobCell  = row.getCell(3).toString()
                    val placeInGroupCell  = row.getCell(4).toString()
                    val phone  = row.getCell(5).toString()
                    var isCommander = jobCell!="לוחם"
                    var isLutenent = jobCell=="סמל" || jobCell=="סמפ"

                    if(!Util.excelArmyJobs.contains(jobCell)){
                        validForm = false
                    }

                    var placeCellList = listOf<String>()
                    if(placeInGroupCell.contains('\\')) {
                        placeCellList = placeInGroupCell.split('\\')
                    }
                    if(placeInGroupCell.contains('/')) {
                        placeCellList = placeInGroupCell.split('/')
                    }
                    if(placeInGroupCell.contains('-')) {
                        placeCellList = placeInGroupCell.split('-')
                    }
                    for(word in placeCellList){
                        if(!Util.excelArmyPositions.contains(word)){
                            validForm = false
                        }
                    }
                    if(validForm) {
                        if (placeCellList.size == 2) {
                            positionMap = Util.getSoldierArmyPosition(placeCellList[1])
                        }
                        if (placeCellList.size == 3) {
                            if (placeCellList[1] == "מחלקה 1") {
                                positionMap = when (placeCellList[2]) {
                                    "כיתה א" -> {
                                        Util.getSoldierArmyPosition("כיתה א מח-1")
                                    }
                                    "כיתה ב" -> {
                                        Util.getSoldierArmyPosition("כיתה ב מח-1")
                                    }
                                    "כיתה ג" -> {
                                        Util.getSoldierArmyPosition("כיתה ג מח-1")
                                    }
                                    else -> {
                                        ""
                                    }
                                }
                            }
                            if (placeCellList[1] == "מחלקה 2") {
                                positionMap = when (placeCellList[2]) {
                                    "כיתה א" -> {
                                        Util.getSoldierArmyPosition("כיתה א מח-2")
                                    }
                                    "כיתה ב" -> {
                                        Util.getSoldierArmyPosition("כיתה ב מח-2")
                                    }
                                    "כיתה ג" -> {
                                        Util.getSoldierArmyPosition("כיתה ג מח-2")
                                    }
                                    else -> {
                                        ""
                                    }
                                }
                            }
                            if (placeCellList[1] == "מחלקה 3") {
                                positionMap = when (placeCellList[2]) {
                                    "כיתה א" -> {
                                        Util.getSoldierArmyPosition("כיתה א מח-3")
                                    }
                                    "כיתה ב" -> {
                                        Util.getSoldierArmyPosition("כיתה ב מח-3")
                                    }
                                    "כיתה ג" -> {
                                        Util.getSoldierArmyPosition("כיתה ג מח-3")
                                    }
                                    else -> {
                                        ""
                                    }
                                }
                            }

                        }
                        if (jobCell == "מפ" || jobCell == "סמפ") {
                            jobCell = "1"
                            positionMap = "1"
                        } else {
                            if (jobCell == "לוחם") {
                                jobCell = ""
                            } else {
                                if (jobCell == "רספ") {
                                    jobCell = "1.3"
                                } else {
                                    jobCell = positionMap
                                    if (isLutenent) {
                                        jobCell = "-$jobCell"
                                    }
                                }
                            }
                        }

                        val soldier = Soldier(
                            "$firstName $lastName", id, "", "", false, "", listOf(),
                            phone, "", jobCell, positionMap, "", isCommander, isLutenent, listOf(), listOf()
                        )
                        if (!soldiers.contains(soldier)) {
                            soldiers.add(soldier)
                        }

                    }else{
                        Toast.makeText(context, "הקובץ אינו תקין, אנא בדוק את ההוראות ונסה שוב", Toast.LENGTH_LONG).show()
                        return emptyList()
                    }
                }


                return soldiers

            }catch (e:Exception){
                Toast.makeText(context, "הקובץ אינו תקין, אנא בדוק את ההוראות ונסה שוב", Toast.LENGTH_SHORT).show()
                return emptyList()
            }
        }
    }
}