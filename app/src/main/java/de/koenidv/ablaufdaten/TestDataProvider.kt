package de.koenidv.ablaufdaten

import java.time.LocalDate

object TestDataProvider {
    val groceriesList = listOf<Grocery>(
        Grocery(0, "Mehl", LocalDate.parse("2022-07-11")!!),
        Grocery(1, "Milch", LocalDate.parse("2022-07-12")!!),
        Grocery(2, "Möhren", LocalDate.parse("2022-07-15")!!),
        Grocery(3, "Äpfel", LocalDate.parse("2022-07-03")!!),
        Grocery(4, "Zucchini", LocalDate.parse("2022-08-21")!!),
    )
}