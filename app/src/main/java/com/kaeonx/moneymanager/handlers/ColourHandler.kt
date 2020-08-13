package com.kaeonx.moneymanager.handlers

import android.content.res.ColorStateList
import com.kaeonx.moneymanager.R
import com.kaeonx.moneymanager.activities.App

class ColourHandler private constructor() {

    companion object {

        ////////////////////////////////////////////////////////////////////////////////
        /**
         * Spinner Manipulation
         */
        ////////////////////////////////////////////////////////////////////////////////

        private val allColourFamilies = listOf(
            "Red",
            "Deep Purple",
            "Light Blue",
            "Green",
            "Yellow",
            "Deep Orange",
            "Blue Grey",
            "Pink",
            "Indigo",
            "Cyan",
            "Light Green",
            "Amber",
            "Brown",
            "Purple",
            "Blue",
            "Teal",
            "Lime",
            "Orange",
            "Grey",
            "Black",
            "White"
        )
        private val allColourIntensities = listOf(
            "50",
            "100",
            "200",
            "300",
            "400",
            "500",
            "600",
            "700",
            "800",
            "900",
            "A100",
            "A200",
            "A400",
            "A700"
        )

        internal fun getColourFamiliesFull(): List<String> {
            return allColourFamilies.toList()
        }

        internal fun getColourFamiliesPartial(): List<String> {
            return ArrayList(allColourFamilies).apply {
                this.remove("Blue Grey")
                this.remove("Brown")
                this.remove("Grey")
            }.toList()
        }

        internal fun getColourIntensitiesFull(): List<String> {
            return allColourIntensities.toList()
        }

        internal fun getColourIntensitiesPartial(): List<String> {
            return ArrayList(allColourIntensities).apply {
                this.remove("A100")
                this.remove("A200")
                this.remove("A400")
                this.remove("A700")
            }.toList()
        }

        ////////////////////////////////////////////////////////////////////////////////
        /**
         * Parsing Colour Strings
         */
        ////////////////////////////////////////////////////////////////////////////////

        internal fun saveColourString(colourFamily: String, colourIntensity: String?): String {
            return if (colourIntensity == null) {
                colourFamily
            } else {
                "$colourFamily,$colourIntensity"
            }
        }

        internal fun readColourFamily(colourString: String): String {
            return when (colourString) {
                "Black", "White" -> colourString
                else -> colourString.split(",")[0]
            }
        }

        internal fun readColourIntensity(colourString: String): String? {
            return when (colourString) {
                "Black", "White" -> null
                else -> colourString.split(",")[1]
            }
        }

        ////////////////////////////////////////////////////////////////////////////////
        /**
         * Color Getters
         */
        ////////////////////////////////////////////////////////////////////////////////

        private fun getObj(resourceId: Int): Int =
            App.context.resources.getColor(resourceId, null)

        internal fun getColourObjectOf(colourString: String): Int = when (colourString) {
            "Red,50" -> getObj(R.color.red_50)
            "Red,100" -> getObj(R.color.red_100)
            "Red,200" -> getObj(R.color.red_200)
            "Red,300" -> getObj(R.color.red_300)
            "Red,400" -> getObj(R.color.red_400)
            "Red,500" -> getObj(R.color.red_500)
            "Red,600" -> getObj(R.color.red_600)
            "Red,700" -> getObj(R.color.red_700)
            "Red,800" -> getObj(R.color.red_800)
            "Red,900" -> getObj(R.color.red_900)
            "Red,A100" -> getObj(R.color.red_A100)
            "Red,A200" -> getObj(R.color.red_A200)
            "Red,A400" -> getObj(R.color.red_A400)
            "Red,A700" -> getObj(R.color.red_A700)

            "Deep Purple,50" -> getObj(R.color.deep_purple_50)
            "Deep Purple,100" -> getObj(R.color.deep_purple_100)
            "Deep Purple,200" -> getObj(R.color.deep_purple_200)
            "Deep Purple,300" -> getObj(R.color.deep_purple_300)
            "Deep Purple,400" -> getObj(R.color.deep_purple_400)
            "Deep Purple,500" -> getObj(R.color.deep_purple_500)
            "Deep Purple,600" -> getObj(R.color.deep_purple_600)
            "Deep Purple,700" -> getObj(R.color.deep_purple_700)
            "Deep Purple,800" -> getObj(R.color.deep_purple_800)
            "Deep Purple,900" -> getObj(R.color.deep_purple_900)
            "Deep Purple,A100" -> getObj(R.color.deep_purple_A100)
            "Deep Purple,A200" -> getObj(R.color.deep_purple_A200)
            "Deep Purple,A400" -> getObj(R.color.deep_purple_A400)
            "Deep Purple,A700" -> getObj(R.color.deep_purple_A700)

            "Light Blue,50" -> getObj(R.color.light_blue_50)
            "Light Blue,100" -> getObj(R.color.light_blue_100)
            "Light Blue,200" -> getObj(R.color.light_blue_200)
            "Light Blue,300" -> getObj(R.color.light_blue_300)
            "Light Blue,400" -> getObj(R.color.light_blue_400)
            "Light Blue,500" -> getObj(R.color.light_blue_500)
            "Light Blue,600" -> getObj(R.color.light_blue_600)
            "Light Blue,700" -> getObj(R.color.light_blue_700)
            "Light Blue,800" -> getObj(R.color.light_blue_800)
            "Light Blue,900" -> getObj(R.color.light_blue_900)
            "Light Blue,A100" -> getObj(R.color.light_blue_A100)
            "Light Blue,A200" -> getObj(R.color.light_blue_A200)
            "Light Blue,A400" -> getObj(R.color.light_blue_A400)
            "Light Blue,A700" -> getObj(R.color.light_blue_A700)

            "Green,50" -> getObj(R.color.green_50)
            "Green,100" -> getObj(R.color.green_100)
            "Green,200" -> getObj(R.color.green_200)
            "Green,300" -> getObj(R.color.green_300)
            "Green,400" -> getObj(R.color.green_400)
            "Green,500" -> getObj(R.color.green_500)
            "Green,600" -> getObj(R.color.green_600)
            "Green,700" -> getObj(R.color.green_700)
            "Green,800" -> getObj(R.color.green_800)
            "Green,900" -> getObj(R.color.green_900)
            "Green,A100" -> getObj(R.color.green_A100)
            "Green,A200" -> getObj(R.color.green_A200)
            "Green,A400" -> getObj(R.color.green_A400)
            "Green,A700" -> getObj(R.color.green_A700)

            "Yellow,50" -> getObj(R.color.yellow_50)
            "Yellow,100" -> getObj(R.color.yellow_100)
            "Yellow,200" -> getObj(R.color.yellow_200)
            "Yellow,300" -> getObj(R.color.yellow_300)
            "Yellow,400" -> getObj(R.color.yellow_400)
            "Yellow,500" -> getObj(R.color.yellow_500)
            "Yellow,600" -> getObj(R.color.yellow_600)
            "Yellow,700" -> getObj(R.color.yellow_700)
            "Yellow,800" -> getObj(R.color.yellow_800)
            "Yellow,900" -> getObj(R.color.yellow_900)
            "Yellow,A100" -> getObj(R.color.yellow_A100)
            "Yellow,A200" -> getObj(R.color.yellow_A200)
            "Yellow,A400" -> getObj(R.color.yellow_A400)
            "Yellow,A700" -> getObj(R.color.yellow_A700)

            "Deep Orange,50" -> getObj(R.color.deep_orange_50)
            "Deep Orange,100" -> getObj(R.color.deep_orange_100)
            "Deep Orange,200" -> getObj(R.color.deep_orange_200)
            "Deep Orange,300" -> getObj(R.color.deep_orange_300)
            "Deep Orange,400" -> getObj(R.color.deep_orange_400)
            "Deep Orange,500" -> getObj(R.color.deep_orange_500)
            "Deep Orange,600" -> getObj(R.color.deep_orange_600)
            "Deep Orange,700" -> getObj(R.color.deep_orange_700)
            "Deep Orange,800" -> getObj(R.color.deep_orange_800)
            "Deep Orange,900" -> getObj(R.color.deep_orange_900)
            "Deep Orange,A100" -> getObj(R.color.deep_orange_A100)
            "Deep Orange,A200" -> getObj(R.color.deep_orange_A200)
            "Deep Orange,A400" -> getObj(R.color.deep_orange_A400)
            "Deep Orange,A700" -> getObj(R.color.deep_orange_A700)

            "Blue Grey,50" -> getObj(R.color.blue_grey_50)
            "Blue Grey,100" -> getObj(R.color.blue_grey_100)
            "Blue Grey,200" -> getObj(R.color.blue_grey_200)
            "Blue Grey,300" -> getObj(R.color.blue_grey_300)
            "Blue Grey,400" -> getObj(R.color.blue_grey_400)
            "Blue Grey,500" -> getObj(R.color.blue_grey_500)
            "Blue Grey,600" -> getObj(R.color.blue_grey_600)
            "Blue Grey,700" -> getObj(R.color.blue_grey_700)
            "Blue Grey,800" -> getObj(R.color.blue_grey_800)
            "Blue Grey,900" -> getObj(R.color.blue_grey_900)

            "Pink,50" -> getObj(R.color.pink_50)
            "Pink,100" -> getObj(R.color.pink_100)
            "Pink,200" -> getObj(R.color.pink_200)
            "Pink,300" -> getObj(R.color.pink_300)
            "Pink,400" -> getObj(R.color.pink_400)
            "Pink,500" -> getObj(R.color.pink_500)
            "Pink,600" -> getObj(R.color.pink_600)
            "Pink,700" -> getObj(R.color.pink_700)
            "Pink,800" -> getObj(R.color.pink_800)
            "Pink,900" -> getObj(R.color.pink_900)
            "Pink,A100" -> getObj(R.color.pink_A100)
            "Pink,A200" -> getObj(R.color.pink_A200)
            "Pink,A400" -> getObj(R.color.pink_A400)
            "Pink,A700" -> getObj(R.color.pink_A700)

            "Indigo,50" -> getObj(R.color.indigo_50)
            "Indigo,100" -> getObj(R.color.indigo_100)
            "Indigo,200" -> getObj(R.color.indigo_200)
            "Indigo,300" -> getObj(R.color.indigo_300)
            "Indigo,400" -> getObj(R.color.indigo_400)
            "Indigo,500" -> getObj(R.color.indigo_500)
            "Indigo,600" -> getObj(R.color.indigo_600)
            "Indigo,700" -> getObj(R.color.indigo_700)
            "Indigo,800" -> getObj(R.color.indigo_800)
            "Indigo,900" -> getObj(R.color.indigo_900)
            "Indigo,A100" -> getObj(R.color.indigo_A100)
            "Indigo,A200" -> getObj(R.color.indigo_A200)
            "Indigo,A400" -> getObj(R.color.indigo_A400)
            "Indigo,A700" -> getObj(R.color.indigo_A700)

            "Cyan,50" -> getObj(R.color.cyan_50)
            "Cyan,100" -> getObj(R.color.cyan_100)
            "Cyan,200" -> getObj(R.color.cyan_200)
            "Cyan,300" -> getObj(R.color.cyan_300)
            "Cyan,400" -> getObj(R.color.cyan_400)
            "Cyan,500" -> getObj(R.color.cyan_500)
            "Cyan,600" -> getObj(R.color.cyan_600)
            "Cyan,700" -> getObj(R.color.cyan_700)
            "Cyan,800" -> getObj(R.color.cyan_800)
            "Cyan,900" -> getObj(R.color.cyan_900)
            "Cyan,A100" -> getObj(R.color.cyan_A100)
            "Cyan,A200" -> getObj(R.color.cyan_A200)
            "Cyan,A400" -> getObj(R.color.cyan_A400)
            "Cyan,A700" -> getObj(R.color.cyan_A700)

            "Light Green,50" -> getObj(R.color.light_green_50)
            "Light Green,100" -> getObj(R.color.light_green_100)
            "Light Green,200" -> getObj(R.color.light_green_200)
            "Light Green,300" -> getObj(R.color.light_green_300)
            "Light Green,400" -> getObj(R.color.light_green_400)
            "Light Green,500" -> getObj(R.color.light_green_500)
            "Light Green,600" -> getObj(R.color.light_green_600)
            "Light Green,700" -> getObj(R.color.light_green_700)
            "Light Green,800" -> getObj(R.color.light_green_800)
            "Light Green,900" -> getObj(R.color.light_green_900)
            "Light Green,A100" -> getObj(R.color.light_green_A100)
            "Light Green,A200" -> getObj(R.color.light_green_A200)
            "Light Green,A400" -> getObj(R.color.light_green_A400)
            "Light Green,A700" -> getObj(R.color.light_green_A700)

            "Amber,50" -> getObj(R.color.amber_50)
            "Amber,100" -> getObj(R.color.amber_100)
            "Amber,200" -> getObj(R.color.amber_200)
            "Amber,300" -> getObj(R.color.amber_300)
            "Amber,400" -> getObj(R.color.amber_400)
            "Amber,500" -> getObj(R.color.amber_500)
            "Amber,600" -> getObj(R.color.amber_600)
            "Amber,700" -> getObj(R.color.amber_700)
            "Amber,800" -> getObj(R.color.amber_800)
            "Amber,900" -> getObj(R.color.amber_900)
            "Amber,A100" -> getObj(R.color.amber_A100)
            "Amber,A200" -> getObj(R.color.amber_A200)
            "Amber,A400" -> getObj(R.color.amber_A400)
            "Amber,A700" -> getObj(R.color.amber_A700)

            "Brown,50" -> getObj(R.color.brown_50)
            "Brown,100" -> getObj(R.color.brown_100)
            "Brown,200" -> getObj(R.color.brown_200)
            "Brown,300" -> getObj(R.color.brown_300)
            "Brown,400" -> getObj(R.color.brown_400)
            "Brown,500" -> getObj(R.color.brown_500)
            "Brown,600" -> getObj(R.color.brown_600)
            "Brown,700" -> getObj(R.color.brown_700)
            "Brown,800" -> getObj(R.color.brown_800)
            "Brown,900" -> getObj(R.color.brown_900)

            "Purple,50" -> getObj(R.color.purple_50)
            "Purple,100" -> getObj(R.color.purple_100)
            "Purple,200" -> getObj(R.color.purple_200)
            "Purple,300" -> getObj(R.color.purple_300)
            "Purple,400" -> getObj(R.color.purple_400)
            "Purple,500" -> getObj(R.color.purple_500)
            "Purple,600" -> getObj(R.color.purple_600)
            "Purple,700" -> getObj(R.color.purple_700)
            "Purple,800" -> getObj(R.color.purple_800)
            "Purple,900" -> getObj(R.color.purple_900)
            "Purple,A100" -> getObj(R.color.purple_A100)
            "Purple,A200" -> getObj(R.color.purple_A200)
            "Purple,A400" -> getObj(R.color.purple_A400)
            "Purple,A700" -> getObj(R.color.purple_A700)

            "Blue,50" -> getObj(R.color.blue_50)
            "Blue,100" -> getObj(R.color.blue_100)
            "Blue,200" -> getObj(R.color.blue_200)
            "Blue,300" -> getObj(R.color.blue_300)
            "Blue,400" -> getObj(R.color.blue_400)
            "Blue,500" -> getObj(R.color.blue_500)
            "Blue,600" -> getObj(R.color.blue_600)
            "Blue,700" -> getObj(R.color.blue_700)
            "Blue,800" -> getObj(R.color.blue_800)
            "Blue,900" -> getObj(R.color.blue_900)
            "Blue,A100" -> getObj(R.color.blue_A100)
            "Blue,A200" -> getObj(R.color.blue_A200)
            "Blue,A400" -> getObj(R.color.blue_A400)
            "Blue,A700" -> getObj(R.color.blue_A700)

            "Teal,50" -> getObj(R.color.teal_50)
            "Teal,100" -> getObj(R.color.teal_100)
            "Teal,200" -> getObj(R.color.teal_200)
            "Teal,300" -> getObj(R.color.teal_300)
            "Teal,400" -> getObj(R.color.teal_400)
            "Teal,500" -> getObj(R.color.teal_500)
            "Teal,600" -> getObj(R.color.teal_600)
            "Teal,700" -> getObj(R.color.teal_700)
            "Teal,800" -> getObj(R.color.teal_800)
            "Teal,900" -> getObj(R.color.teal_900)
            "Teal,A100" -> getObj(R.color.teal_A100)
            "Teal,A200" -> getObj(R.color.teal_A200)
            "Teal,A400" -> getObj(R.color.teal_A400)
            "Teal,A700" -> getObj(R.color.teal_A700)

            "Lime,50" -> getObj(R.color.lime_50)
            "Lime,100" -> getObj(R.color.lime_100)
            "Lime,200" -> getObj(R.color.lime_200)
            "Lime,300" -> getObj(R.color.lime_300)
            "Lime,400" -> getObj(R.color.lime_400)
            "Lime,500" -> getObj(R.color.lime_500)
            "Lime,600" -> getObj(R.color.lime_600)
            "Lime,700" -> getObj(R.color.lime_700)
            "Lime,800" -> getObj(R.color.lime_800)
            "Lime,900" -> getObj(R.color.lime_900)
            "Lime,A100" -> getObj(R.color.lime_A100)
            "Lime,A200" -> getObj(R.color.lime_A200)
            "Lime,A400" -> getObj(R.color.lime_A400)
            "Lime,A700" -> getObj(R.color.lime_A700)

            "Orange,50" -> getObj(R.color.orange_50)
            "Orange,100" -> getObj(R.color.orange_100)
            "Orange,200" -> getObj(R.color.orange_200)
            "Orange,300" -> getObj(R.color.orange_300)
            "Orange,400" -> getObj(R.color.orange_400)
            "Orange,500" -> getObj(R.color.orange_500)
            "Orange,600" -> getObj(R.color.orange_600)
            "Orange,700" -> getObj(R.color.orange_700)
            "Orange,800" -> getObj(R.color.orange_800)
            "Orange,900" -> getObj(R.color.orange_900)
            "Orange,A100" -> getObj(R.color.orange_A100)
            "Orange,A200" -> getObj(R.color.orange_A200)
            "Orange,A400" -> getObj(R.color.orange_A400)
            "Orange,A700" -> getObj(R.color.orange_A700)

            "Grey,50" -> getObj(R.color.grey_50)
            "Grey,100" -> getObj(R.color.grey_100)
            "Grey,200" -> getObj(R.color.grey_200)
            "Grey,300" -> getObj(R.color.grey_300)
            "Grey,400" -> getObj(R.color.grey_400)
            "Grey,500" -> getObj(R.color.grey_500)
            "Grey,600" -> getObj(R.color.grey_600)
            "Grey,700" -> getObj(R.color.grey_700)
            "Grey,800" -> getObj(R.color.grey_800)
            "Grey,900" -> getObj(R.color.grey_900)

            "Black" -> getObj(R.color.black)
            "White" -> getObj(R.color.white)
            "TRANSPARENT" -> getObj(R.color.black)
            else -> throw IllegalArgumentException("Unknown colourString $colourString.")
        }

        private fun getColourObjectOn(colourString: String): Int = when (colourString) {
            "Red,50" -> getObj(R.color.black)
            "Red,100" -> getObj(R.color.black)
            "Red,200" -> getObj(R.color.black)
            "Red,300" -> getObj(R.color.black)
            "Red,400" -> getObj(R.color.black)
            "Red,500" -> getObj(R.color.black)
            "Red,600" -> getObj(R.color.black)
            "Red,700" -> getObj(R.color.white)
            "Red,800" -> getObj(R.color.white)
            "Red,900" -> getObj(R.color.white)
            "Red,A100" -> getObj(R.color.black)
            "Red,A200" -> getObj(R.color.black)
            "Red,A400" -> getObj(R.color.black)
            "Red,A700" -> getObj(R.color.white)

            "Deep Purple,50" -> getObj(R.color.black)
            "Deep Purple,100" -> getObj(R.color.black)
            "Deep Purple,200" -> getObj(R.color.black)
            "Deep Purple,300" -> getObj(R.color.black)
            "Deep Purple,400" -> getObj(R.color.white)
            "Deep Purple,500" -> getObj(R.color.white)
            "Deep Purple,600" -> getObj(R.color.white)
            "Deep Purple,700" -> getObj(R.color.white)
            "Deep Purple,800" -> getObj(R.color.white)
            "Deep Purple,900" -> getObj(R.color.white)
            "Deep Purple,A100" -> getObj(R.color.black)
            "Deep Purple,A200" -> getObj(R.color.white)
            "Deep Purple,A400" -> getObj(R.color.white)
            "Deep Purple,A700" -> getObj(R.color.white)

            "Light Blue,50" -> getObj(R.color.black)
            "Light Blue,100" -> getObj(R.color.black)
            "Light Blue,200" -> getObj(R.color.black)
            "Light Blue,300" -> getObj(R.color.black)
            "Light Blue,400" -> getObj(R.color.black)
            "Light Blue,500" -> getObj(R.color.black)
            "Light Blue,600" -> getObj(R.color.black)
            "Light Blue,700" -> getObj(R.color.black)
            "Light Blue,800" -> getObj(R.color.white)
            "Light Blue,900" -> getObj(R.color.white)
            "Light Blue,A100" -> getObj(R.color.black)
            "Light Blue,A200" -> getObj(R.color.black)
            "Light Blue,A400" -> getObj(R.color.black)
            "Light Blue,A700" -> getObj(R.color.black)

            "Green,50" -> getObj(R.color.black)
            "Green,100" -> getObj(R.color.black)
            "Green,200" -> getObj(R.color.black)
            "Green,300" -> getObj(R.color.black)
            "Green,400" -> getObj(R.color.black)
            "Green,500" -> getObj(R.color.black)
            "Green,600" -> getObj(R.color.black)
            "Green,700" -> getObj(R.color.black)
            "Green,800" -> getObj(R.color.white)
            "Green,900" -> getObj(R.color.white)
            "Green,A100" -> getObj(R.color.black)
            "Green,A200" -> getObj(R.color.black)
            "Green,A400" -> getObj(R.color.black)
            "Green,A700" -> getObj(R.color.black)

            "Yellow,50" -> getObj(R.color.black)
            "Yellow,100" -> getObj(R.color.black)
            "Yellow,200" -> getObj(R.color.black)
            "Yellow,300" -> getObj(R.color.black)
            "Yellow,400" -> getObj(R.color.black)
            "Yellow,500" -> getObj(R.color.black)
            "Yellow,600" -> getObj(R.color.black)
            "Yellow,700" -> getObj(R.color.black)
            "Yellow,800" -> getObj(R.color.black)
            "Yellow,900" -> getObj(R.color.black)
            "Yellow,A100" -> getObj(R.color.black)
            "Yellow,A200" -> getObj(R.color.black)
            "Yellow,A400" -> getObj(R.color.black)
            "Yellow,A700" -> getObj(R.color.black)

            "Deep Orange,50" -> getObj(R.color.black)
            "Deep Orange,100" -> getObj(R.color.black)
            "Deep Orange,200" -> getObj(R.color.black)
            "Deep Orange,300" -> getObj(R.color.black)
            "Deep Orange,400" -> getObj(R.color.black)
            "Deep Orange,500" -> getObj(R.color.black)
            "Deep Orange,600" -> getObj(R.color.black)
            "Deep Orange,700" -> getObj(R.color.black)
            "Deep Orange,800" -> getObj(R.color.black)
            "Deep Orange,900" -> getObj(R.color.white)
            "Deep Orange,A100" -> getObj(R.color.black)
            "Deep Orange,A200" -> getObj(R.color.black)
            "Deep Orange,A400" -> getObj(R.color.black)
            "Deep Orange,A700" -> getObj(R.color.white)

            "Blue Grey,50" -> getObj(R.color.black)
            "Blue Grey,100" -> getObj(R.color.black)
            "Blue Grey,200" -> getObj(R.color.black)
            "Blue Grey,300" -> getObj(R.color.black)
            "Blue Grey,400" -> getObj(R.color.black)
            "Blue Grey,500" -> getObj(R.color.black)
            "Blue Grey,600" -> getObj(R.color.white)
            "Blue Grey,700" -> getObj(R.color.white)
            "Blue Grey,800" -> getObj(R.color.white)
            "Blue Grey,900" -> getObj(R.color.white)

            "Pink,50" -> getObj(R.color.black)
            "Pink,100" -> getObj(R.color.black)
            "Pink,200" -> getObj(R.color.black)
            "Pink,300" -> getObj(R.color.black)
            "Pink,400" -> getObj(R.color.black)
            "Pink,500" -> getObj(R.color.black)
            "Pink,600" -> getObj(R.color.white)
            "Pink,700" -> getObj(R.color.white)
            "Pink,800" -> getObj(R.color.white)
            "Pink,900" -> getObj(R.color.white)
            "Pink,A100" -> getObj(R.color.black)
            "Pink,A200" -> getObj(R.color.black)
            "Pink,A400" -> getObj(R.color.black)
            "Pink,A700" -> getObj(R.color.white)

            "Indigo,50" -> getObj(R.color.black)
            "Indigo,100" -> getObj(R.color.black)
            "Indigo,200" -> getObj(R.color.black)
            "Indigo,300" -> getObj(R.color.black)
            "Indigo,400" -> getObj(R.color.white)
            "Indigo,500" -> getObj(R.color.white)
            "Indigo,600" -> getObj(R.color.white)
            "Indigo,700" -> getObj(R.color.white)
            "Indigo,800" -> getObj(R.color.white)
            "Indigo,900" -> getObj(R.color.white)
            "Indigo,A100" -> getObj(R.color.black)
            "Indigo,A200" -> getObj(R.color.black)
            "Indigo,A400" -> getObj(R.color.white)
            "Indigo,A700" -> getObj(R.color.white)

            "Cyan,50" -> getObj(R.color.black)
            "Cyan,100" -> getObj(R.color.black)
            "Cyan,200" -> getObj(R.color.black)
            "Cyan,300" -> getObj(R.color.black)
            "Cyan,400" -> getObj(R.color.black)
            "Cyan,500" -> getObj(R.color.black)
            "Cyan,600" -> getObj(R.color.black)
            "Cyan,700" -> getObj(R.color.black)
            "Cyan,800" -> getObj(R.color.white)
            "Cyan,900" -> getObj(R.color.white)
            "Cyan,A100" -> getObj(R.color.black)
            "Cyan,A200" -> getObj(R.color.black)
            "Cyan,A400" -> getObj(R.color.black)
            "Cyan,A700" -> getObj(R.color.black)

            "Light Green,50" -> getObj(R.color.black)
            "Light Green,100" -> getObj(R.color.black)
            "Light Green,200" -> getObj(R.color.black)
            "Light Green,300" -> getObj(R.color.black)
            "Light Green,400" -> getObj(R.color.black)
            "Light Green,500" -> getObj(R.color.black)
            "Light Green,600" -> getObj(R.color.black)
            "Light Green,700" -> getObj(R.color.black)
            "Light Green,800" -> getObj(R.color.black)
            "Light Green,900" -> getObj(R.color.white)
            "Light Green,A100" -> getObj(R.color.black)
            "Light Green,A200" -> getObj(R.color.black)
            "Light Green,A400" -> getObj(R.color.black)
            "Light Green,A700" -> getObj(R.color.black)

            "Amber,50" -> getObj(R.color.black)
            "Amber,100" -> getObj(R.color.black)
            "Amber,200" -> getObj(R.color.black)
            "Amber,300" -> getObj(R.color.black)
            "Amber,400" -> getObj(R.color.black)
            "Amber,500" -> getObj(R.color.black)
            "Amber,600" -> getObj(R.color.black)
            "Amber,700" -> getObj(R.color.black)
            "Amber,800" -> getObj(R.color.black)
            "Amber,900" -> getObj(R.color.black)
            "Amber,A100" -> getObj(R.color.black)
            "Amber,A200" -> getObj(R.color.black)
            "Amber,A400" -> getObj(R.color.black)
            "Amber,A700" -> getObj(R.color.black)

            "Brown,50" -> getObj(R.color.black)
            "Brown,100" -> getObj(R.color.black)
            "Brown,200" -> getObj(R.color.black)
            "Brown,300" -> getObj(R.color.black)
            "Brown,400" -> getObj(R.color.white)
            "Brown,500" -> getObj(R.color.white)
            "Brown,600" -> getObj(R.color.white)
            "Brown,700" -> getObj(R.color.white)
            "Brown,800" -> getObj(R.color.white)
            "Brown,900" -> getObj(R.color.white)

            "Purple,50" -> getObj(R.color.black)
            "Purple,100" -> getObj(R.color.black)
            "Purple,200" -> getObj(R.color.black)
            "Purple,300" -> getObj(R.color.black)
            "Purple,400" -> getObj(R.color.white)
            "Purple,500" -> getObj(R.color.white)
            "Purple,600" -> getObj(R.color.white)
            "Purple,700" -> getObj(R.color.white)
            "Purple,800" -> getObj(R.color.white)
            "Purple,900" -> getObj(R.color.white)
            "Purple,A100" -> getObj(R.color.black)
            "Purple,A200" -> getObj(R.color.black)
            "Purple,A400" -> getObj(R.color.black)
            "Purple,A700" -> getObj(R.color.white)

            "Blue,50" -> getObj(R.color.black)
            "Blue,100" -> getObj(R.color.black)
            "Blue,200" -> getObj(R.color.black)
            "Blue,300" -> getObj(R.color.black)
            "Blue,400" -> getObj(R.color.black)
            "Blue,500" -> getObj(R.color.black)
            "Blue,600" -> getObj(R.color.black)
            "Blue,700" -> getObj(R.color.white)
            "Blue,800" -> getObj(R.color.white)
            "Blue,900" -> getObj(R.color.white)
            "Blue,A100" -> getObj(R.color.black)
            "Blue,A200" -> getObj(R.color.black)
            "Blue,A400" -> getObj(R.color.black)
            "Blue,A700" -> getObj(R.color.white)

            "Teal,50" -> getObj(R.color.black)
            "Teal,100" -> getObj(R.color.black)
            "Teal,200" -> getObj(R.color.black)
            "Teal,300" -> getObj(R.color.black)
            "Teal,400" -> getObj(R.color.black)
            "Teal,500" -> getObj(R.color.black)
            "Teal,600" -> getObj(R.color.black)
            "Teal,700" -> getObj(R.color.white)
            "Teal,800" -> getObj(R.color.white)
            "Teal,900" -> getObj(R.color.white)
            "Teal,A100" -> getObj(R.color.black)
            "Teal,A200" -> getObj(R.color.black)
            "Teal,A400" -> getObj(R.color.black)
            "Teal,A700" -> getObj(R.color.black)

            "Lime,50" -> getObj(R.color.black)
            "Lime,100" -> getObj(R.color.black)
            "Lime,200" -> getObj(R.color.black)
            "Lime,300" -> getObj(R.color.black)
            "Lime,400" -> getObj(R.color.black)
            "Lime,500" -> getObj(R.color.black)
            "Lime,600" -> getObj(R.color.black)
            "Lime,700" -> getObj(R.color.black)
            "Lime,800" -> getObj(R.color.black)
            "Lime,900" -> getObj(R.color.white)
            "Lime,A100" -> getObj(R.color.black)
            "Lime,A200" -> getObj(R.color.black)
            "Lime,A400" -> getObj(R.color.black)
            "Lime,A700" -> getObj(R.color.black)

            "Orange,50" -> getObj(R.color.black)
            "Orange,100" -> getObj(R.color.black)
            "Orange,200" -> getObj(R.color.black)
            "Orange,300" -> getObj(R.color.black)
            "Orange,400" -> getObj(R.color.black)
            "Orange,500" -> getObj(R.color.black)
            "Orange,600" -> getObj(R.color.black)
            "Orange,700" -> getObj(R.color.black)
            "Orange,800" -> getObj(R.color.black)
            "Orange,900" -> getObj(R.color.black)
            "Orange,A100" -> getObj(R.color.black)
            "Orange,A200" -> getObj(R.color.black)
            "Orange,A400" -> getObj(R.color.black)
            "Orange,A700" -> getObj(R.color.black)

            "Grey,50" -> getObj(R.color.black)
            "Grey,100" -> getObj(R.color.black)
            "Grey,200" -> getObj(R.color.black)
            "Grey,300" -> getObj(R.color.black)
            "Grey,400" -> getObj(R.color.black)
            "Grey,500" -> getObj(R.color.black)
            "Grey,600" -> getObj(R.color.white)
            "Grey,700" -> getObj(R.color.white)
            "Grey,800" -> getObj(R.color.white)
            "Grey,900" -> getObj(R.color.white)

            "Black" -> getObj(R.color.white)
            "White" -> getObj(R.color.black)
            "TRANSPARENT" -> getObj(R.color.white)
            else -> throw IllegalArgumentException("Unknown colourString $colourString.")
        }

        ////////////////////////////////////////////////////////////////////////////////
        /**
         * ColorStateList Getters
         */
        ////////////////////////////////////////////////////////////////////////////////

        private fun getCSL(resourceId: Int): ColorStateList =
            ColorStateList.valueOf(getObj(resourceId))

        internal fun getColorStateListOf(colourString: String): ColorStateList =
            ColorStateList.valueOf(getColourObjectOf(colourString))

        internal fun getColorStateListOf(
            colourFamily: String,
            colourIntensity: String?
        ): ColorStateList =
            ColorStateList.valueOf(
                getColourObjectOf(saveColourString(colourFamily, colourIntensity))
            )

        internal fun getColorStateListOn(colourString: String): ColorStateList =
            ColorStateList.valueOf(getColourObjectOn(colourString))

        internal fun getColorStateListOn(
            colourFamily: String,
            colourIntensity: String?
        ): ColorStateList =
            ColorStateList.valueOf(
                getColourObjectOn(saveColourString(colourFamily, colourIntensity))
            )

//        internal fun getColorStateListOn(
//            colourFamily: String,
//            colourIntensity: String?
//        ): ColorStateList =
//            when (colourFamily) {
//                "Red", "Blue" -> {
//                    when (colourIntensity) {
//                        "50", "100", "200", "300", "400", "500", "600",
//                        "A100", "A200", "A400" -> getCSL(R.color.black)
//                        "700", "800", "900",
//                        "A700" -> getCSL(R.color.white)
//                        else -> throw IllegalArgumentException("Unknown colourFamily $colourFamily and colourIntensity $colourIntensity combination.")
//                    }
//                }
//                "Pink" -> {
//                    when (colourIntensity) {
//                        "50", "100", "200", "300", "400", "500",
//                        "A100", "A200", "A400" -> getCSL(R.color.black)
//                        "600", "700", "800", "900",
//                        "A700" -> getCSL(R.color.white)
//                        else -> throw IllegalArgumentException("Unknown colourFamily $colourFamily and colourIntensity $colourIntensity combination.")
//                    }
//                }
//                "Purple" -> {
//                    when (colourIntensity) {
//                        "50", "100", "200", "300",
//                        "A100", "A200", "A400" -> getCSL(R.color.black)
//                        "400", "500", "600", "700", "800", "900",
//                        "A700" -> getCSL(R.color.white)
//                        else -> throw IllegalArgumentException("Unknown colourFamily $colourFamily and colourIntensity $colourIntensity combination.")
//                    }
//                }
//                "Deep Purple" -> {
//                    when (colourIntensity) {
//                        "50", "100", "200", "300",
//                        "A100" -> getCSL(R.color.black)
//                        "400", "500", "600", "700", "800", "900",
//                        "A200", "A400", "A700" -> getCSL(R.color.white)
//                        else -> throw IllegalArgumentException("Unknown colourFamily $colourFamily and colourIntensity $colourIntensity combination.")
//                    }
//                }
//                "Indigo" -> {
//                    when (colourIntensity) {
//                        "50", "100", "200", "300",
//                        "A100", "A200" -> getCSL(R.color.black)
//                        "400", "500", "600", "700", "800", "900",
//                        "A400", "A700" -> getCSL(R.color.white)
//                        else -> throw IllegalArgumentException("Unknown colourFamily $colourFamily and colourIntensity $colourIntensity combination.")
//                    }
//                }
//                "Light Blue", "Cyan", "Green" -> {
//                    when (colourIntensity) {
//                        "50", "100", "200", "300", "400", "500", "600", "700",
//                        "A100", "A200", "A400", "A700" -> getCSL(R.color.black)
//                        "800", "900" -> getCSL(R.color.white)
//                        else -> throw IllegalArgumentException("Unknown colourFamily $colourFamily and colourIntensity $colourIntensity combination.")
//                    }
//                }
//                "Teal" -> {
//                    when (colourIntensity) {
//                        "50", "100", "200", "300", "400", "500", "600",
//                        "A100", "A200", "A400", "A700" -> getCSL(R.color.black)
//                        "700", "800", "900" -> getCSL(R.color.white)
//                        else -> throw IllegalArgumentException("Unknown colourFamily $colourFamily and colourIntensity $colourIntensity combination.")
//                    }
//                }
//                "Light Green", "Lime" -> {
//                    when (colourIntensity) {
//                        "50", "100", "200", "300", "400", "500", "600", "700", "800",
//                        "A100", "A200", "A400", "A700" -> getCSL(R.color.black)
//                        "900" -> getCSL(R.color.white)
//                        else -> throw IllegalArgumentException("Unknown colourFamily $colourFamily and colourIntensity $colourIntensity combination.")
//                    }
//                }
//                "Yellow", "Amber", "Orange" -> {
//                    when (colourIntensity) {
//                        in allColourIntensities -> getCSL(R.color.black)
//                        else -> throw IllegalArgumentException("Unknown colourFamily $colourFamily and colourIntensity $colourIntensity combination.")
//                    }
//                }
//                "Deep Orange" -> {
//                    when (colourIntensity) {
//                        "50", "100", "200", "300", "400", "500", "600", "700", "800",
//                        "A100", "A200", "A400" -> getCSL(R.color.black)
//                        "900",
//                        "A700" -> getCSL(R.color.white)
//                        else -> throw IllegalArgumentException("Unknown colourFamily $colourFamily and colourIntensity $colourIntensity combination.")
//                    }
//                }
//                "Brown" -> {
//                    when (colourIntensity) {
//                        "50", "100", "200", "300" -> getCSL(R.color.black)
//                        "400", "500", "600", "700", "800", "900" -> getCSL(R.color.white)
//                        else -> throw IllegalArgumentException("Unknown colourFamily $colourFamily and colourIntensity $colourIntensity combination.")
//                    }
//                }
//                "Grey", "Blue Grey" -> {
//                    when (colourIntensity) {
//                        "50", "100", "200", "300", "400", "500" -> getCSL(R.color.black)
//                        "600", "700", "800", "900" -> getCSL(R.color.white)
//                        else -> throw IllegalArgumentException("Unknown colourFamily $colourFamily and colourIntensity $colourIntensity combination.")
//                    }
//                }
//                "Black" -> getCSL(R.color.white)
//                "White" -> getCSL(R.color.black)
//                "TRANSPARENT" -> getCSL(R.color.white)
//                else -> throw IllegalArgumentException("Unknown colourFamily $colourFamily and colourIntensity $colourIntensity combination.")
//            }

    }
}