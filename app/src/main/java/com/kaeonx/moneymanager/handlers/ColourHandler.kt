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

        fun saveColourString(colourFamily: String, colourIntensity: String?): String {
            return if (colourIntensity == null) {
                colourFamily
            } else {
                "$colourFamily,$colourIntensity"
            }
        }

        fun readColourFamily(colourString: String): String {
            return when (colourString) {
                "Black", "White" -> colourString
                else -> colourString.split(",")[0]
            }
        }

        fun readColourIntensity(colourString: String): String? {
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

        fun getColourObject(colourString: String) = when (colourString) {
            "Red,50" -> App.context.resources.getColor(R.color.red_50, null)
            "Red,100" -> App.context.resources.getColor(R.color.red_100, null)
            "Red,200" -> App.context.resources.getColor(R.color.red_200, null)
            "Red,300" -> App.context.resources.getColor(R.color.red_300, null)
            "Red,400" -> App.context.resources.getColor(R.color.red_400, null)
            "Red,500" -> App.context.resources.getColor(R.color.red_500, null)
            "Red,600" -> App.context.resources.getColor(R.color.red_600, null)
            "Red,700" -> App.context.resources.getColor(R.color.red_700, null)
            "Red,800" -> App.context.resources.getColor(R.color.red_800, null)
            "Red,900" -> App.context.resources.getColor(R.color.red_900, null)
            "Red,A100" -> App.context.resources.getColor(R.color.red_A100, null)
            "Red,A200" -> App.context.resources.getColor(R.color.red_A200, null)
            "Red,A400" -> App.context.resources.getColor(R.color.red_A400, null)
            "Red,A700" -> App.context.resources.getColor(R.color.red_A700, null)

            "Deep Purple,50" -> App.context.resources.getColor(R.color.deep_purple_50, null)
            "Deep Purple,100" -> App.context.resources.getColor(R.color.deep_purple_100, null)
            "Deep Purple,200" -> App.context.resources.getColor(R.color.deep_purple_200, null)
            "Deep Purple,300" -> App.context.resources.getColor(R.color.deep_purple_300, null)
            "Deep Purple,400" -> App.context.resources.getColor(R.color.deep_purple_400, null)
            "Deep Purple,500" -> App.context.resources.getColor(R.color.deep_purple_500, null)
            "Deep Purple,600" -> App.context.resources.getColor(R.color.deep_purple_600, null)
            "Deep Purple,700" -> App.context.resources.getColor(R.color.deep_purple_700, null)
            "Deep Purple,800" -> App.context.resources.getColor(R.color.deep_purple_800, null)
            "Deep Purple,900" -> App.context.resources.getColor(R.color.deep_purple_900, null)
            "Deep Purple,A100" -> App.context.resources.getColor(R.color.deep_purple_A100, null)
            "Deep Purple,A200" -> App.context.resources.getColor(R.color.deep_purple_A200, null)
            "Deep Purple,A400" -> App.context.resources.getColor(R.color.deep_purple_A400, null)
            "Deep Purple,A700" -> App.context.resources.getColor(R.color.deep_purple_A700, null)

            "Light Blue,50" -> App.context.resources.getColor(R.color.light_blue_50, null)
            "Light Blue,100" -> App.context.resources.getColor(R.color.light_blue_100, null)
            "Light Blue,200" -> App.context.resources.getColor(R.color.light_blue_200, null)
            "Light Blue,300" -> App.context.resources.getColor(R.color.light_blue_300, null)
            "Light Blue,400" -> App.context.resources.getColor(R.color.light_blue_400, null)
            "Light Blue,500" -> App.context.resources.getColor(R.color.light_blue_500, null)
            "Light Blue,600" -> App.context.resources.getColor(R.color.light_blue_600, null)
            "Light Blue,700" -> App.context.resources.getColor(R.color.light_blue_700, null)
            "Light Blue,800" -> App.context.resources.getColor(R.color.light_blue_800, null)
            "Light Blue,900" -> App.context.resources.getColor(R.color.light_blue_900, null)
            "Light Blue,A100" -> App.context.resources.getColor(R.color.light_blue_A100, null)
            "Light Blue,A200" -> App.context.resources.getColor(R.color.light_blue_A200, null)
            "Light Blue,A400" -> App.context.resources.getColor(R.color.light_blue_A400, null)
            "Light Blue,A700" -> App.context.resources.getColor(R.color.light_blue_A700, null)

            "Green,50" -> App.context.resources.getColor(R.color.green_50, null)
            "Green,100" -> App.context.resources.getColor(R.color.green_100, null)
            "Green,200" -> App.context.resources.getColor(R.color.green_200, null)
            "Green,300" -> App.context.resources.getColor(R.color.green_300, null)
            "Green,400" -> App.context.resources.getColor(R.color.green_400, null)
            "Green,500" -> App.context.resources.getColor(R.color.green_500, null)
            "Green,600" -> App.context.resources.getColor(R.color.green_600, null)
            "Green,700" -> App.context.resources.getColor(R.color.green_700, null)
            "Green,800" -> App.context.resources.getColor(R.color.green_800, null)
            "Green,900" -> App.context.resources.getColor(R.color.green_900, null)
            "Green,A100" -> App.context.resources.getColor(R.color.green_A100, null)
            "Green,A200" -> App.context.resources.getColor(R.color.green_A200, null)
            "Green,A400" -> App.context.resources.getColor(R.color.green_A400, null)
            "Green,A700" -> App.context.resources.getColor(R.color.green_A700, null)

            "Yellow,50" -> App.context.resources.getColor(R.color.yellow_50, null)
            "Yellow,100" -> App.context.resources.getColor(R.color.yellow_100, null)
            "Yellow,200" -> App.context.resources.getColor(R.color.yellow_200, null)
            "Yellow,300" -> App.context.resources.getColor(R.color.yellow_300, null)
            "Yellow,400" -> App.context.resources.getColor(R.color.yellow_400, null)
            "Yellow,500" -> App.context.resources.getColor(R.color.yellow_500, null)
            "Yellow,600" -> App.context.resources.getColor(R.color.yellow_600, null)
            "Yellow,700" -> App.context.resources.getColor(R.color.yellow_700, null)
            "Yellow,800" -> App.context.resources.getColor(R.color.yellow_800, null)
            "Yellow,900" -> App.context.resources.getColor(R.color.yellow_900, null)
            "Yellow,A100" -> App.context.resources.getColor(R.color.yellow_A100, null)
            "Yellow,A200" -> App.context.resources.getColor(R.color.yellow_A200, null)
            "Yellow,A400" -> App.context.resources.getColor(R.color.yellow_A400, null)
            "Yellow,A700" -> App.context.resources.getColor(R.color.yellow_A700, null)

            "Deep Orange,50" -> App.context.resources.getColor(R.color.deep_orange_50, null)
            "Deep Orange,100" -> App.context.resources.getColor(R.color.deep_orange_100, null)
            "Deep Orange,200" -> App.context.resources.getColor(R.color.deep_orange_200, null)
            "Deep Orange,300" -> App.context.resources.getColor(R.color.deep_orange_300, null)
            "Deep Orange,400" -> App.context.resources.getColor(R.color.deep_orange_400, null)
            "Deep Orange,500" -> App.context.resources.getColor(R.color.deep_orange_500, null)
            "Deep Orange,600" -> App.context.resources.getColor(R.color.deep_orange_600, null)
            "Deep Orange,700" -> App.context.resources.getColor(R.color.deep_orange_700, null)
            "Deep Orange,800" -> App.context.resources.getColor(R.color.deep_orange_800, null)
            "Deep Orange,900" -> App.context.resources.getColor(R.color.deep_orange_900, null)
            "Deep Orange,A100" -> App.context.resources.getColor(R.color.deep_orange_A100, null)
            "Deep Orange,A200" -> App.context.resources.getColor(R.color.deep_orange_A200, null)
            "Deep Orange,A400" -> App.context.resources.getColor(R.color.deep_orange_A400, null)
            "Deep Orange,A700" -> App.context.resources.getColor(R.color.deep_orange_A700, null)

            "Blue Grey,50" -> App.context.resources.getColor(R.color.blue_grey_50, null)
            "Blue Grey,100" -> App.context.resources.getColor(R.color.blue_grey_100, null)
            "Blue Grey,200" -> App.context.resources.getColor(R.color.blue_grey_200, null)
            "Blue Grey,300" -> App.context.resources.getColor(R.color.blue_grey_300, null)
            "Blue Grey,400" -> App.context.resources.getColor(R.color.blue_grey_400, null)
            "Blue Grey,500" -> App.context.resources.getColor(R.color.blue_grey_500, null)
            "Blue Grey,600" -> App.context.resources.getColor(R.color.blue_grey_600, null)
            "Blue Grey,700" -> App.context.resources.getColor(R.color.blue_grey_700, null)
            "Blue Grey,800" -> App.context.resources.getColor(R.color.blue_grey_800, null)
            "Blue Grey,900" -> App.context.resources.getColor(R.color.blue_grey_900, null)

            "Pink,50" -> App.context.resources.getColor(R.color.pink_50, null)
            "Pink,100" -> App.context.resources.getColor(R.color.pink_100, null)
            "Pink,200" -> App.context.resources.getColor(R.color.pink_200, null)
            "Pink,300" -> App.context.resources.getColor(R.color.pink_300, null)
            "Pink,400" -> App.context.resources.getColor(R.color.pink_400, null)
            "Pink,500" -> App.context.resources.getColor(R.color.pink_500, null)
            "Pink,600" -> App.context.resources.getColor(R.color.pink_600, null)
            "Pink,700" -> App.context.resources.getColor(R.color.pink_700, null)
            "Pink,800" -> App.context.resources.getColor(R.color.pink_800, null)
            "Pink,900" -> App.context.resources.getColor(R.color.pink_900, null)
            "Pink,A100" -> App.context.resources.getColor(R.color.pink_A100, null)
            "Pink,A200" -> App.context.resources.getColor(R.color.pink_A200, null)
            "Pink,A400" -> App.context.resources.getColor(R.color.pink_A400, null)
            "Pink,A700" -> App.context.resources.getColor(R.color.pink_A700, null)

            "Indigo,50" -> App.context.resources.getColor(R.color.indigo_50, null)
            "Indigo,100" -> App.context.resources.getColor(R.color.indigo_100, null)
            "Indigo,200" -> App.context.resources.getColor(R.color.indigo_200, null)
            "Indigo,300" -> App.context.resources.getColor(R.color.indigo_300, null)
            "Indigo,400" -> App.context.resources.getColor(R.color.indigo_400, null)
            "Indigo,500" -> App.context.resources.getColor(R.color.indigo_500, null)
            "Indigo,600" -> App.context.resources.getColor(R.color.indigo_600, null)
            "Indigo,700" -> App.context.resources.getColor(R.color.indigo_700, null)
            "Indigo,800" -> App.context.resources.getColor(R.color.indigo_800, null)
            "Indigo,900" -> App.context.resources.getColor(R.color.indigo_900, null)
            "Indigo,A100" -> App.context.resources.getColor(R.color.indigo_A100, null)
            "Indigo,A200" -> App.context.resources.getColor(R.color.indigo_A200, null)
            "Indigo,A400" -> App.context.resources.getColor(R.color.indigo_A400, null)
            "Indigo,A700" -> App.context.resources.getColor(R.color.indigo_A700, null)

            "Cyan,50" -> App.context.resources.getColor(R.color.cyan_50, null)
            "Cyan,100" -> App.context.resources.getColor(R.color.cyan_100, null)
            "Cyan,200" -> App.context.resources.getColor(R.color.cyan_200, null)
            "Cyan,300" -> App.context.resources.getColor(R.color.cyan_300, null)
            "Cyan,400" -> App.context.resources.getColor(R.color.cyan_400, null)
            "Cyan,500" -> App.context.resources.getColor(R.color.cyan_500, null)
            "Cyan,600" -> App.context.resources.getColor(R.color.cyan_600, null)
            "Cyan,700" -> App.context.resources.getColor(R.color.cyan_700, null)
            "Cyan,800" -> App.context.resources.getColor(R.color.cyan_800, null)
            "Cyan,900" -> App.context.resources.getColor(R.color.cyan_900, null)
            "Cyan,A100" -> App.context.resources.getColor(R.color.cyan_A100, null)
            "Cyan,A200" -> App.context.resources.getColor(R.color.cyan_A200, null)
            "Cyan,A400" -> App.context.resources.getColor(R.color.cyan_A400, null)
            "Cyan,A700" -> App.context.resources.getColor(R.color.cyan_A700, null)

            "Light Green,50" -> App.context.resources.getColor(R.color.light_green_50, null)
            "Light Green,100" -> App.context.resources.getColor(R.color.light_green_100, null)
            "Light Green,200" -> App.context.resources.getColor(R.color.light_green_200, null)
            "Light Green,300" -> App.context.resources.getColor(R.color.light_green_300, null)
            "Light Green,400" -> App.context.resources.getColor(R.color.light_green_400, null)
            "Light Green,500" -> App.context.resources.getColor(R.color.light_green_500, null)
            "Light Green,600" -> App.context.resources.getColor(R.color.light_green_600, null)
            "Light Green,700" -> App.context.resources.getColor(R.color.light_green_700, null)
            "Light Green,800" -> App.context.resources.getColor(R.color.light_green_800, null)
            "Light Green,900" -> App.context.resources.getColor(R.color.light_green_900, null)
            "Light Green,A100" -> App.context.resources.getColor(R.color.light_green_A100, null)
            "Light Green,A200" -> App.context.resources.getColor(R.color.light_green_A200, null)
            "Light Green,A400" -> App.context.resources.getColor(R.color.light_green_A400, null)
            "Light Green,A700" -> App.context.resources.getColor(R.color.light_green_A700, null)

            "Amber,50" -> App.context.resources.getColor(R.color.amber_50, null)
            "Amber,100" -> App.context.resources.getColor(R.color.amber_100, null)
            "Amber,200" -> App.context.resources.getColor(R.color.amber_200, null)
            "Amber,300" -> App.context.resources.getColor(R.color.amber_300, null)
            "Amber,400" -> App.context.resources.getColor(R.color.amber_400, null)
            "Amber,500" -> App.context.resources.getColor(R.color.amber_500, null)
            "Amber,600" -> App.context.resources.getColor(R.color.amber_600, null)
            "Amber,700" -> App.context.resources.getColor(R.color.amber_700, null)
            "Amber,800" -> App.context.resources.getColor(R.color.amber_800, null)
            "Amber,900" -> App.context.resources.getColor(R.color.amber_900, null)
            "Amber,A100" -> App.context.resources.getColor(R.color.amber_A100, null)
            "Amber,A200" -> App.context.resources.getColor(R.color.amber_A200, null)
            "Amber,A400" -> App.context.resources.getColor(R.color.amber_A400, null)
            "Amber,A700" -> App.context.resources.getColor(R.color.amber_A700, null)

            "Brown,50" -> App.context.resources.getColor(R.color.brown_50, null)
            "Brown,100" -> App.context.resources.getColor(R.color.brown_100, null)
            "Brown,200" -> App.context.resources.getColor(R.color.brown_200, null)
            "Brown,300" -> App.context.resources.getColor(R.color.brown_300, null)
            "Brown,400" -> App.context.resources.getColor(R.color.brown_400, null)
            "Brown,500" -> App.context.resources.getColor(R.color.brown_500, null)
            "Brown,600" -> App.context.resources.getColor(R.color.brown_600, null)
            "Brown,700" -> App.context.resources.getColor(R.color.brown_700, null)
            "Brown,800" -> App.context.resources.getColor(R.color.brown_800, null)
            "Brown,900" -> App.context.resources.getColor(R.color.brown_900, null)

            "Purple,50" -> App.context.resources.getColor(R.color.purple_50, null)
            "Purple,100" -> App.context.resources.getColor(R.color.purple_100, null)
            "Purple,200" -> App.context.resources.getColor(R.color.purple_200, null)
            "Purple,300" -> App.context.resources.getColor(R.color.purple_300, null)
            "Purple,400" -> App.context.resources.getColor(R.color.purple_400, null)
            "Purple,500" -> App.context.resources.getColor(R.color.purple_500, null)
            "Purple,600" -> App.context.resources.getColor(R.color.purple_600, null)
            "Purple,700" -> App.context.resources.getColor(R.color.purple_700, null)
            "Purple,800" -> App.context.resources.getColor(R.color.purple_800, null)
            "Purple,900" -> App.context.resources.getColor(R.color.purple_900, null)
            "Purple,A100" -> App.context.resources.getColor(R.color.purple_A100, null)
            "Purple,A200" -> App.context.resources.getColor(R.color.purple_A200, null)
            "Purple,A400" -> App.context.resources.getColor(R.color.purple_A400, null)
            "Purple,A700" -> App.context.resources.getColor(R.color.purple_A700, null)

            "Blue,50" -> App.context.resources.getColor(R.color.blue_50, null)
            "Blue,100" -> App.context.resources.getColor(R.color.blue_100, null)
            "Blue,200" -> App.context.resources.getColor(R.color.blue_200, null)
            "Blue,300" -> App.context.resources.getColor(R.color.blue_300, null)
            "Blue,400" -> App.context.resources.getColor(R.color.blue_400, null)
            "Blue,500" -> App.context.resources.getColor(R.color.blue_500, null)
            "Blue,600" -> App.context.resources.getColor(R.color.blue_600, null)
            "Blue,700" -> App.context.resources.getColor(R.color.blue_700, null)
            "Blue,800" -> App.context.resources.getColor(R.color.blue_800, null)
            "Blue,900" -> App.context.resources.getColor(R.color.blue_900, null)
            "Blue,A100" -> App.context.resources.getColor(R.color.blue_A100, null)
            "Blue,A200" -> App.context.resources.getColor(R.color.blue_A200, null)
            "Blue,A400" -> App.context.resources.getColor(R.color.blue_A400, null)
            "Blue,A700" -> App.context.resources.getColor(R.color.blue_A700, null)

            "Teal,50" -> App.context.resources.getColor(R.color.teal_50, null)
            "Teal,100" -> App.context.resources.getColor(R.color.teal_100, null)
            "Teal,200" -> App.context.resources.getColor(R.color.teal_200, null)
            "Teal,300" -> App.context.resources.getColor(R.color.teal_300, null)
            "Teal,400" -> App.context.resources.getColor(R.color.teal_400, null)
            "Teal,500" -> App.context.resources.getColor(R.color.teal_500, null)
            "Teal,600" -> App.context.resources.getColor(R.color.teal_600, null)
            "Teal,700" -> App.context.resources.getColor(R.color.teal_700, null)
            "Teal,800" -> App.context.resources.getColor(R.color.teal_800, null)
            "Teal,900" -> App.context.resources.getColor(R.color.teal_900, null)
            "Teal,A100" -> App.context.resources.getColor(R.color.teal_A100, null)
            "Teal,A200" -> App.context.resources.getColor(R.color.teal_A200, null)
            "Teal,A400" -> App.context.resources.getColor(R.color.teal_A400, null)
            "Teal,A700" -> App.context.resources.getColor(R.color.teal_A700, null)

            "Lime,50" -> App.context.resources.getColor(R.color.lime_50, null)
            "Lime,100" -> App.context.resources.getColor(R.color.lime_100, null)
            "Lime,200" -> App.context.resources.getColor(R.color.lime_200, null)
            "Lime,300" -> App.context.resources.getColor(R.color.lime_300, null)
            "Lime,400" -> App.context.resources.getColor(R.color.lime_400, null)
            "Lime,500" -> App.context.resources.getColor(R.color.lime_500, null)
            "Lime,600" -> App.context.resources.getColor(R.color.lime_600, null)
            "Lime,700" -> App.context.resources.getColor(R.color.lime_700, null)
            "Lime,800" -> App.context.resources.getColor(R.color.lime_800, null)
            "Lime,900" -> App.context.resources.getColor(R.color.lime_900, null)
            "Lime,A100" -> App.context.resources.getColor(R.color.lime_A100, null)
            "Lime,A200" -> App.context.resources.getColor(R.color.lime_A200, null)
            "Lime,A400" -> App.context.resources.getColor(R.color.lime_A400, null)
            "Lime,A700" -> App.context.resources.getColor(R.color.lime_A700, null)

            "Orange,50" -> App.context.resources.getColor(R.color.orange_50, null)
            "Orange,100" -> App.context.resources.getColor(R.color.orange_100, null)
            "Orange,200" -> App.context.resources.getColor(R.color.orange_200, null)
            "Orange,300" -> App.context.resources.getColor(R.color.orange_300, null)
            "Orange,400" -> App.context.resources.getColor(R.color.orange_400, null)
            "Orange,500" -> App.context.resources.getColor(R.color.orange_500, null)
            "Orange,600" -> App.context.resources.getColor(R.color.orange_600, null)
            "Orange,700" -> App.context.resources.getColor(R.color.orange_700, null)
            "Orange,800" -> App.context.resources.getColor(R.color.orange_800, null)
            "Orange,900" -> App.context.resources.getColor(R.color.orange_900, null)
            "Orange,A100" -> App.context.resources.getColor(R.color.orange_A100, null)
            "Orange,A200" -> App.context.resources.getColor(R.color.orange_A200, null)
            "Orange,A400" -> App.context.resources.getColor(R.color.orange_A400, null)
            "Orange,A700" -> App.context.resources.getColor(R.color.orange_A700, null)

            "Grey,50" -> App.context.resources.getColor(R.color.grey_50, null)
            "Grey,100" -> App.context.resources.getColor(R.color.grey_100, null)
            "Grey,200" -> App.context.resources.getColor(R.color.grey_200, null)
            "Grey,300" -> App.context.resources.getColor(R.color.grey_300, null)
            "Grey,400" -> App.context.resources.getColor(R.color.grey_400, null)
            "Grey,500" -> App.context.resources.getColor(R.color.grey_500, null)
            "Grey,600" -> App.context.resources.getColor(R.color.grey_600, null)
            "Grey,700" -> App.context.resources.getColor(R.color.grey_700, null)
            "Grey,800" -> App.context.resources.getColor(R.color.grey_800, null)
            "Grey,900" -> App.context.resources.getColor(R.color.grey_900, null)

            "Black" -> App.context.resources.getColor(R.color.black, null)
            "White" -> App.context.resources.getColor(R.color.white, null)

            else -> App.context.resources.getColor(R.color.black, null)
        }

        fun getColourObject(colourFamily: String, colourIntensity: String?) = when (colourFamily) {
            "Red" -> {
                when (colourIntensity) {
                    "50" -> App.context.resources.getColor(R.color.red_50, null)
                    "100" -> App.context.resources.getColor(R.color.red_100, null)
                    "200" -> App.context.resources.getColor(R.color.red_200, null)
                    "300" -> App.context.resources.getColor(R.color.red_300, null)
                    "400" -> App.context.resources.getColor(R.color.red_400, null)
                    "500" -> App.context.resources.getColor(R.color.red_500, null)
                    "600" -> App.context.resources.getColor(R.color.red_600, null)
                    "700" -> App.context.resources.getColor(R.color.red_700, null)
                    "800" -> App.context.resources.getColor(R.color.red_800, null)
                    "900" -> App.context.resources.getColor(R.color.red_900, null)
                    "A100" -> App.context.resources.getColor(R.color.red_A100, null)
                    "A200" -> App.context.resources.getColor(R.color.red_A200, null)
                    "A400" -> App.context.resources.getColor(R.color.red_A400, null)
                    "A700" -> App.context.resources.getColor(R.color.red_A700, null)
                    else -> App.context.resources.getColor(R.color.black, null)
                }
            }
            "Deep Purple" -> {
                when (colourIntensity) {
                    "50" -> App.context.resources.getColor(R.color.deep_purple_50, null)
                    "100" -> App.context.resources.getColor(R.color.deep_purple_100, null)
                    "200" -> App.context.resources.getColor(R.color.deep_purple_200, null)
                    "300" -> App.context.resources.getColor(R.color.deep_purple_300, null)
                    "400" -> App.context.resources.getColor(R.color.deep_purple_400, null)
                    "500" -> App.context.resources.getColor(R.color.deep_purple_500, null)
                    "600" -> App.context.resources.getColor(R.color.deep_purple_600, null)
                    "700" -> App.context.resources.getColor(R.color.deep_purple_700, null)
                    "800" -> App.context.resources.getColor(R.color.deep_purple_800, null)
                    "900" -> App.context.resources.getColor(R.color.deep_purple_900, null)
                    "A100" -> App.context.resources.getColor(R.color.deep_purple_A100, null)
                    "A200" -> App.context.resources.getColor(R.color.deep_purple_A200, null)
                    "A400" -> App.context.resources.getColor(R.color.deep_purple_A400, null)
                    "A700" -> App.context.resources.getColor(R.color.deep_purple_A700, null)
                    else -> App.context.resources.getColor(R.color.black, null)
                }
            }
            "Light Blue" -> {
                when (colourIntensity) {
                    "50" -> App.context.resources.getColor(R.color.light_blue_50, null)
                    "100" -> App.context.resources.getColor(R.color.light_blue_100, null)
                    "200" -> App.context.resources.getColor(R.color.light_blue_200, null)
                    "300" -> App.context.resources.getColor(R.color.light_blue_300, null)
                    "400" -> App.context.resources.getColor(R.color.light_blue_400, null)
                    "500" -> App.context.resources.getColor(R.color.light_blue_500, null)
                    "600" -> App.context.resources.getColor(R.color.light_blue_600, null)
                    "700" -> App.context.resources.getColor(R.color.light_blue_700, null)
                    "800" -> App.context.resources.getColor(R.color.light_blue_800, null)
                    "900" -> App.context.resources.getColor(R.color.light_blue_900, null)
                    "A100" -> App.context.resources.getColor(R.color.light_blue_A100, null)
                    "A200" -> App.context.resources.getColor(R.color.light_blue_A200, null)
                    "A400" -> App.context.resources.getColor(R.color.light_blue_A400, null)
                    "A700" -> App.context.resources.getColor(R.color.light_blue_A700, null)
                    else -> App.context.resources.getColor(R.color.black, null)
                }
            }
            "Green" -> {
                when (colourIntensity) {
                    "50" -> App.context.resources.getColor(R.color.green_50, null)
                    "100" -> App.context.resources.getColor(R.color.green_100, null)
                    "200" -> App.context.resources.getColor(R.color.green_200, null)
                    "300" -> App.context.resources.getColor(R.color.green_300, null)
                    "400" -> App.context.resources.getColor(R.color.green_400, null)
                    "500" -> App.context.resources.getColor(R.color.green_500, null)
                    "600" -> App.context.resources.getColor(R.color.green_600, null)
                    "700" -> App.context.resources.getColor(R.color.green_700, null)
                    "800" -> App.context.resources.getColor(R.color.green_800, null)
                    "900" -> App.context.resources.getColor(R.color.green_900, null)
                    "A100" -> App.context.resources.getColor(R.color.green_A100, null)
                    "A200" -> App.context.resources.getColor(R.color.green_A200, null)
                    "A400" -> App.context.resources.getColor(R.color.green_A400, null)
                    "A700" -> App.context.resources.getColor(R.color.green_A700, null)
                    else -> App.context.resources.getColor(R.color.black, null)
                }
            }
            "Yellow" -> {
                when (colourIntensity) {
                    "50" -> App.context.resources.getColor(R.color.yellow_50, null)
                    "100" -> App.context.resources.getColor(R.color.yellow_100, null)
                    "200" -> App.context.resources.getColor(R.color.yellow_200, null)
                    "300" -> App.context.resources.getColor(R.color.yellow_300, null)
                    "400" -> App.context.resources.getColor(R.color.yellow_400, null)
                    "500" -> App.context.resources.getColor(R.color.yellow_500, null)
                    "600" -> App.context.resources.getColor(R.color.yellow_600, null)
                    "700" -> App.context.resources.getColor(R.color.yellow_700, null)
                    "800" -> App.context.resources.getColor(R.color.yellow_800, null)
                    "900" -> App.context.resources.getColor(R.color.yellow_900, null)
                    "A100" -> App.context.resources.getColor(R.color.yellow_A100, null)
                    "A200" -> App.context.resources.getColor(R.color.yellow_A200, null)
                    "A400" -> App.context.resources.getColor(R.color.yellow_A400, null)
                    "A700" -> App.context.resources.getColor(R.color.yellow_A700, null)
                    else -> App.context.resources.getColor(R.color.black, null)
                }
            }
            "Deep Orange" -> {
                when (colourIntensity) {
                    "50" -> App.context.resources.getColor(R.color.deep_orange_50, null)
                    "100" -> App.context.resources.getColor(R.color.deep_orange_100, null)
                    "200" -> App.context.resources.getColor(R.color.deep_orange_200, null)
                    "300" -> App.context.resources.getColor(R.color.deep_orange_300, null)
                    "400" -> App.context.resources.getColor(R.color.deep_orange_400, null)
                    "500" -> App.context.resources.getColor(R.color.deep_orange_500, null)
                    "600" -> App.context.resources.getColor(R.color.deep_orange_600, null)
                    "700" -> App.context.resources.getColor(R.color.deep_orange_700, null)
                    "800" -> App.context.resources.getColor(R.color.deep_orange_800, null)
                    "900" -> App.context.resources.getColor(R.color.deep_orange_900, null)
                    "A100" -> App.context.resources.getColor(R.color.deep_orange_A100, null)
                    "A200" -> App.context.resources.getColor(R.color.deep_orange_A200, null)
                    "A400" -> App.context.resources.getColor(R.color.deep_orange_A400, null)
                    "A700" -> App.context.resources.getColor(R.color.deep_orange_A700, null)
                    else -> App.context.resources.getColor(R.color.black, null)
                }
            }
            "Blue Grey" -> {
                when (colourIntensity) {
                    "50" -> App.context.resources.getColor(R.color.blue_grey_50, null)
                    "100" -> App.context.resources.getColor(R.color.blue_grey_100, null)
                    "200" -> App.context.resources.getColor(R.color.blue_grey_200, null)
                    "300" -> App.context.resources.getColor(R.color.blue_grey_300, null)
                    "400" -> App.context.resources.getColor(R.color.blue_grey_400, null)
                    "500" -> App.context.resources.getColor(R.color.blue_grey_500, null)
                    "600" -> App.context.resources.getColor(R.color.blue_grey_600, null)
                    "700" -> App.context.resources.getColor(R.color.blue_grey_700, null)
                    "800" -> App.context.resources.getColor(R.color.blue_grey_800, null)
                    "900" -> App.context.resources.getColor(R.color.blue_grey_900, null)
                    else -> App.context.resources.getColor(R.color.black, null)
                }
            }
            "Pink" -> {
                when (colourIntensity) {
                    "50" -> App.context.resources.getColor(R.color.pink_50, null)
                    "100" -> App.context.resources.getColor(R.color.pink_100, null)
                    "200" -> App.context.resources.getColor(R.color.pink_200, null)
                    "300" -> App.context.resources.getColor(R.color.pink_300, null)
                    "400" -> App.context.resources.getColor(R.color.pink_400, null)
                    "500" -> App.context.resources.getColor(R.color.pink_500, null)
                    "600" -> App.context.resources.getColor(R.color.pink_600, null)
                    "700" -> App.context.resources.getColor(R.color.pink_700, null)
                    "800" -> App.context.resources.getColor(R.color.pink_800, null)
                    "900" -> App.context.resources.getColor(R.color.pink_900, null)
                    "A100" -> App.context.resources.getColor(R.color.pink_A100, null)
                    "A200" -> App.context.resources.getColor(R.color.pink_A200, null)
                    "A400" -> App.context.resources.getColor(R.color.pink_A400, null)
                    "A700" -> App.context.resources.getColor(R.color.pink_A700, null)
                    else -> App.context.resources.getColor(R.color.black, null)
                }
            }
            "Indigo" -> {
                when (colourIntensity) {
                    "50" -> App.context.resources.getColor(R.color.indigo_50, null)
                    "100" -> App.context.resources.getColor(R.color.indigo_100, null)
                    "200" -> App.context.resources.getColor(R.color.indigo_200, null)
                    "300" -> App.context.resources.getColor(R.color.indigo_300, null)
                    "400" -> App.context.resources.getColor(R.color.indigo_400, null)
                    "500" -> App.context.resources.getColor(R.color.indigo_500, null)
                    "600" -> App.context.resources.getColor(R.color.indigo_600, null)
                    "700" -> App.context.resources.getColor(R.color.indigo_700, null)
                    "800" -> App.context.resources.getColor(R.color.indigo_800, null)
                    "900" -> App.context.resources.getColor(R.color.indigo_900, null)
                    "A100" -> App.context.resources.getColor(R.color.indigo_A100, null)
                    "A200" -> App.context.resources.getColor(R.color.indigo_A200, null)
                    "A400" -> App.context.resources.getColor(R.color.indigo_A400, null)
                    "A700" -> App.context.resources.getColor(R.color.indigo_A700, null)
                    else -> App.context.resources.getColor(R.color.black, null)
                }
            }
            "Cyan" -> {
                when (colourIntensity) {
                    "50" -> App.context.resources.getColor(R.color.cyan_50, null)
                    "100" -> App.context.resources.getColor(R.color.cyan_100, null)
                    "200" -> App.context.resources.getColor(R.color.cyan_200, null)
                    "300" -> App.context.resources.getColor(R.color.cyan_300, null)
                    "400" -> App.context.resources.getColor(R.color.cyan_400, null)
                    "500" -> App.context.resources.getColor(R.color.cyan_500, null)
                    "600" -> App.context.resources.getColor(R.color.cyan_600, null)
                    "700" -> App.context.resources.getColor(R.color.cyan_700, null)
                    "800" -> App.context.resources.getColor(R.color.cyan_800, null)
                    "900" -> App.context.resources.getColor(R.color.cyan_900, null)
                    "A100" -> App.context.resources.getColor(R.color.cyan_A100, null)
                    "A200" -> App.context.resources.getColor(R.color.cyan_A200, null)
                    "A400" -> App.context.resources.getColor(R.color.cyan_A400, null)
                    "A700" -> App.context.resources.getColor(R.color.cyan_A700, null)
                    else -> App.context.resources.getColor(R.color.black, null)
                }
            }
            "Light Green" -> {
                when (colourIntensity) {
                    "50" -> App.context.resources.getColor(R.color.light_green_50, null)
                    "100" -> App.context.resources.getColor(R.color.light_green_100, null)
                    "200" -> App.context.resources.getColor(R.color.light_green_200, null)
                    "300" -> App.context.resources.getColor(R.color.light_green_300, null)
                    "400" -> App.context.resources.getColor(R.color.light_green_400, null)
                    "500" -> App.context.resources.getColor(R.color.light_green_500, null)
                    "600" -> App.context.resources.getColor(R.color.light_green_600, null)
                    "700" -> App.context.resources.getColor(R.color.light_green_700, null)
                    "800" -> App.context.resources.getColor(R.color.light_green_800, null)
                    "900" -> App.context.resources.getColor(R.color.light_green_900, null)
                    "A100" -> App.context.resources.getColor(R.color.light_green_A100, null)
                    "A200" -> App.context.resources.getColor(R.color.light_green_A200, null)
                    "A400" -> App.context.resources.getColor(R.color.light_green_A400, null)
                    "A700" -> App.context.resources.getColor(R.color.light_green_A700, null)
                    else -> App.context.resources.getColor(R.color.black, null)
                }
            }
            "Amber" -> {
                when (colourIntensity) {
                    "50" -> App.context.resources.getColor(R.color.amber_50, null)
                    "100" -> App.context.resources.getColor(R.color.amber_100, null)
                    "200" -> App.context.resources.getColor(R.color.amber_200, null)
                    "300" -> App.context.resources.getColor(R.color.amber_300, null)
                    "400" -> App.context.resources.getColor(R.color.amber_400, null)
                    "500" -> App.context.resources.getColor(R.color.amber_500, null)
                    "600" -> App.context.resources.getColor(R.color.amber_600, null)
                    "700" -> App.context.resources.getColor(R.color.amber_700, null)
                    "800" -> App.context.resources.getColor(R.color.amber_800, null)
                    "900" -> App.context.resources.getColor(R.color.amber_900, null)
                    "A100" -> App.context.resources.getColor(R.color.amber_A100, null)
                    "A200" -> App.context.resources.getColor(R.color.amber_A200, null)
                    "A400" -> App.context.resources.getColor(R.color.amber_A400, null)
                    "A700" -> App.context.resources.getColor(R.color.amber_A700, null)
                    else -> App.context.resources.getColor(R.color.black, null)
                }
            }
            "Brown" -> {
                when (colourIntensity) {
                    "50" -> App.context.resources.getColor(R.color.brown_50, null)
                    "100" -> App.context.resources.getColor(R.color.brown_100, null)
                    "200" -> App.context.resources.getColor(R.color.brown_200, null)
                    "300" -> App.context.resources.getColor(R.color.brown_300, null)
                    "400" -> App.context.resources.getColor(R.color.brown_400, null)
                    "500" -> App.context.resources.getColor(R.color.brown_500, null)
                    "600" -> App.context.resources.getColor(R.color.brown_600, null)
                    "700" -> App.context.resources.getColor(R.color.brown_700, null)
                    "800" -> App.context.resources.getColor(R.color.brown_800, null)
                    "900" -> App.context.resources.getColor(R.color.brown_900, null)
                    else -> App.context.resources.getColor(R.color.black, null)
                }
            }
            "Purple" -> {
                when (colourIntensity) {
                    "50" -> App.context.resources.getColor(R.color.purple_50, null)
                    "100" -> App.context.resources.getColor(R.color.purple_100, null)
                    "200" -> App.context.resources.getColor(R.color.purple_200, null)
                    "300" -> App.context.resources.getColor(R.color.purple_300, null)
                    "400" -> App.context.resources.getColor(R.color.purple_400, null)
                    "500" -> App.context.resources.getColor(R.color.purple_500, null)
                    "600" -> App.context.resources.getColor(R.color.purple_600, null)
                    "700" -> App.context.resources.getColor(R.color.purple_700, null)
                    "800" -> App.context.resources.getColor(R.color.purple_800, null)
                    "900" -> App.context.resources.getColor(R.color.purple_900, null)
                    "A100" -> App.context.resources.getColor(R.color.purple_A100, null)
                    "A200" -> App.context.resources.getColor(R.color.purple_A200, null)
                    "A400" -> App.context.resources.getColor(R.color.purple_A400, null)
                    "A700" -> App.context.resources.getColor(R.color.purple_A700, null)
                    else -> App.context.resources.getColor(R.color.black, null)
                }
            }
            "Blue" -> {
                when (colourIntensity) {
                    "50" -> App.context.resources.getColor(R.color.blue_50, null)
                    "100" -> App.context.resources.getColor(R.color.blue_100, null)
                    "200" -> App.context.resources.getColor(R.color.blue_200, null)
                    "300" -> App.context.resources.getColor(R.color.blue_300, null)
                    "400" -> App.context.resources.getColor(R.color.blue_400, null)
                    "500" -> App.context.resources.getColor(R.color.blue_500, null)
                    "600" -> App.context.resources.getColor(R.color.blue_600, null)
                    "700" -> App.context.resources.getColor(R.color.blue_700, null)
                    "800" -> App.context.resources.getColor(R.color.blue_800, null)
                    "900" -> App.context.resources.getColor(R.color.blue_900, null)
                    "A100" -> App.context.resources.getColor(R.color.blue_A100, null)
                    "A200" -> App.context.resources.getColor(R.color.blue_A200, null)
                    "A400" -> App.context.resources.getColor(R.color.blue_A400, null)
                    "A700" -> App.context.resources.getColor(R.color.blue_A700, null)
                    else -> App.context.resources.getColor(R.color.black, null)
                }
            }
            "Teal" -> {
                when (colourIntensity) {
                    "50" -> App.context.resources.getColor(R.color.teal_50, null)
                    "100" -> App.context.resources.getColor(R.color.teal_100, null)
                    "200" -> App.context.resources.getColor(R.color.teal_200, null)
                    "300" -> App.context.resources.getColor(R.color.teal_300, null)
                    "400" -> App.context.resources.getColor(R.color.teal_400, null)
                    "500" -> App.context.resources.getColor(R.color.teal_500, null)
                    "600" -> App.context.resources.getColor(R.color.teal_600, null)
                    "700" -> App.context.resources.getColor(R.color.teal_700, null)
                    "800" -> App.context.resources.getColor(R.color.teal_800, null)
                    "900" -> App.context.resources.getColor(R.color.teal_900, null)
                    "A100" -> App.context.resources.getColor(R.color.teal_A100, null)
                    "A200" -> App.context.resources.getColor(R.color.teal_A200, null)
                    "A400" -> App.context.resources.getColor(R.color.teal_A400, null)
                    "A700" -> App.context.resources.getColor(R.color.teal_A700, null)
                    else -> App.context.resources.getColor(R.color.black, null)
                }
            }
            "Lime" -> {
                when (colourIntensity) {
                    "50" -> App.context.resources.getColor(R.color.lime_50, null)
                    "100" -> App.context.resources.getColor(R.color.lime_100, null)
                    "200" -> App.context.resources.getColor(R.color.lime_200, null)
                    "300" -> App.context.resources.getColor(R.color.lime_300, null)
                    "400" -> App.context.resources.getColor(R.color.lime_400, null)
                    "500" -> App.context.resources.getColor(R.color.lime_500, null)
                    "600" -> App.context.resources.getColor(R.color.lime_600, null)
                    "700" -> App.context.resources.getColor(R.color.lime_700, null)
                    "800" -> App.context.resources.getColor(R.color.lime_800, null)
                    "900" -> App.context.resources.getColor(R.color.lime_900, null)
                    "A100" -> App.context.resources.getColor(R.color.lime_A100, null)
                    "A200" -> App.context.resources.getColor(R.color.lime_A200, null)
                    "A400" -> App.context.resources.getColor(R.color.lime_A400, null)
                    "A700" -> App.context.resources.getColor(R.color.lime_A700, null)
                    else -> App.context.resources.getColor(R.color.black, null)
                }
            }
            "Orange" -> {
                when (colourIntensity) {
                    "50" -> App.context.resources.getColor(R.color.orange_50, null)
                    "100" -> App.context.resources.getColor(R.color.orange_100, null)
                    "200" -> App.context.resources.getColor(R.color.orange_200, null)
                    "300" -> App.context.resources.getColor(R.color.orange_300, null)
                    "400" -> App.context.resources.getColor(R.color.orange_400, null)
                    "500" -> App.context.resources.getColor(R.color.orange_500, null)
                    "600" -> App.context.resources.getColor(R.color.orange_600, null)
                    "700" -> App.context.resources.getColor(R.color.orange_700, null)
                    "800" -> App.context.resources.getColor(R.color.orange_800, null)
                    "900" -> App.context.resources.getColor(R.color.orange_900, null)
                    "A100" -> App.context.resources.getColor(R.color.orange_A100, null)
                    "A200" -> App.context.resources.getColor(R.color.orange_A200, null)
                    "A400" -> App.context.resources.getColor(R.color.orange_A400, null)
                    "A700" -> App.context.resources.getColor(R.color.orange_A700, null)
                    else -> App.context.resources.getColor(R.color.black, null)
                }
            }
            "Grey" -> {
                when (colourIntensity) {
                    "50" -> App.context.resources.getColor(R.color.grey_50, null)
                    "100" -> App.context.resources.getColor(R.color.grey_100, null)
                    "200" -> App.context.resources.getColor(R.color.grey_200, null)
                    "300" -> App.context.resources.getColor(R.color.grey_300, null)
                    "400" -> App.context.resources.getColor(R.color.grey_400, null)
                    "500" -> App.context.resources.getColor(R.color.grey_500, null)
                    "600" -> App.context.resources.getColor(R.color.grey_600, null)
                    "700" -> App.context.resources.getColor(R.color.grey_700, null)
                    "800" -> App.context.resources.getColor(R.color.grey_800, null)
                    "900" -> App.context.resources.getColor(R.color.grey_900, null)
                    else -> App.context.resources.getColor(R.color.black, null)
                }
            }
            "Black" -> App.context.resources.getColor(R.color.black, null)
            "White" -> App.context.resources.getColor(R.color.white, null)
            else -> App.context.resources.getColor(R.color.black, null)
        }

        ////////////////////////////////////////////////////////////////////////////////
        /**
         * ColorStateList Getters
         */
        ////////////////////////////////////////////////////////////////////////////////

        fun getColorStateList(colourString: String): ColorStateList = when (colourString) {
            "Red,50" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.red_50, null))
            "Red,100" -> ColorStateList.valueOf(
                App.context.resources.getColor(
                    R.color.red_100,
                    null
                )
            )
            "Red,200" -> ColorStateList.valueOf(
                App.context.resources.getColor(
                    R.color.red_200,
                    null
                )
            )
            "Red,300" -> ColorStateList.valueOf(
                App.context.resources.getColor(
                    R.color.red_300,
                    null
                )
            )
            "Red,400" -> ColorStateList.valueOf(
                App.context.resources.getColor(
                    R.color.red_400,
                    null
                )
            )
            "Red,500" -> ColorStateList.valueOf(
                App.context.resources.getColor(
                    R.color.red_500,
                    null
                )
            )
            "Red,600" -> ColorStateList.valueOf(
                App.context.resources.getColor(
                    R.color.red_600,
                    null
                )
            )
            "Red,700" -> ColorStateList.valueOf(
                App.context.resources.getColor(
                    R.color.red_700,
                    null
                )
            )
            "Red,800" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.red_800, null))
            "Red,900" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.red_900, null))
            "Red,A100" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.red_A100, null))
            "Red,A200" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.red_A200, null))
            "Red,A400" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.red_A400, null))
            "Red,A700" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.red_A700, null))

            "Deep Purple,50" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.deep_purple_50, null))
            "Deep Purple,100" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.deep_purple_100, null))
            "Deep Purple,200" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.deep_purple_200, null))
            "Deep Purple,300" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.deep_purple_300, null))
            "Deep Purple,400" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.deep_purple_400, null))
            "Deep Purple,500" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.deep_purple_500, null))
            "Deep Purple,600" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.deep_purple_600, null))
            "Deep Purple,700" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.deep_purple_700, null))
            "Deep Purple,800" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.deep_purple_800, null))
            "Deep Purple,900" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.deep_purple_900, null))
            "Deep Purple,A100" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.deep_purple_A100, null))
            "Deep Purple,A200" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.deep_purple_A200, null))
            "Deep Purple,A400" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.deep_purple_A400, null))
            "Deep Purple,A700" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.deep_purple_A700, null))

            "Light Blue,50" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.light_blue_50, null))
            "Light Blue,100" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.light_blue_100, null))
            "Light Blue,200" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.light_blue_200, null))
            "Light Blue,300" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.light_blue_300, null))
            "Light Blue,400" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.light_blue_400, null))
            "Light Blue,500" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.light_blue_500, null))
            "Light Blue,600" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.light_blue_600, null))
            "Light Blue,700" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.light_blue_700, null))
            "Light Blue,800" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.light_blue_800, null))
            "Light Blue,900" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.light_blue_900, null))
            "Light Blue,A100" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.light_blue_A100, null))
            "Light Blue,A200" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.light_blue_A200, null))
            "Light Blue,A400" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.light_blue_A400, null))
            "Light Blue,A700" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.light_blue_A700, null))

            "Green,50" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.green_50, null))
            "Green,100" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.green_100, null))
            "Green,200" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.green_200, null))
            "Green,300" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.green_300, null))
            "Green,400" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.green_400, null))
            "Green,500" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.green_500, null))
            "Green,600" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.green_600, null))
            "Green,700" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.green_700, null))
            "Green,800" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.green_800, null))
            "Green,900" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.green_900, null))
            "Green,A100" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.green_A100, null))
            "Green,A200" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.green_A200, null))
            "Green,A400" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.green_A400, null))
            "Green,A700" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.green_A700, null))

            "Yellow,50" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.yellow_50, null))
            "Yellow,100" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.yellow_100, null))
            "Yellow,200" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.yellow_200, null))
            "Yellow,300" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.yellow_300, null))
            "Yellow,400" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.yellow_400, null))
            "Yellow,500" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.yellow_500, null))
            "Yellow,600" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.yellow_600, null))
            "Yellow,700" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.yellow_700, null))
            "Yellow,800" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.yellow_800, null))
            "Yellow,900" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.yellow_900, null))
            "Yellow,A100" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.yellow_A100, null))
            "Yellow,A200" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.yellow_A200, null))
            "Yellow,A400" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.yellow_A400, null))
            "Yellow,A700" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.yellow_A700, null))

            "Deep Orange,50" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.deep_orange_50, null))
            "Deep Orange,100" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.deep_orange_100, null))
            "Deep Orange,200" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.deep_orange_200, null))
            "Deep Orange,300" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.deep_orange_300, null))
            "Deep Orange,400" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.deep_orange_400, null))
            "Deep Orange,500" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.deep_orange_500, null))
            "Deep Orange,600" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.deep_orange_600, null))
            "Deep Orange,700" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.deep_orange_700, null))
            "Deep Orange,800" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.deep_orange_800, null))
            "Deep Orange,900" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.deep_orange_900, null))
            "Deep Orange,A100" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.deep_orange_A100, null))
            "Deep Orange,A200" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.deep_orange_A200, null))
            "Deep Orange,A400" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.deep_orange_A400, null))
            "Deep Orange,A700" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.deep_orange_A700, null))

            "Blue Grey,50" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.blue_grey_50, null))
            "Blue Grey,100" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.blue_grey_100, null))
            "Blue Grey,200" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.blue_grey_200, null))
            "Blue Grey,300" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.blue_grey_300, null))
            "Blue Grey,400" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.blue_grey_400, null))
            "Blue Grey,500" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.blue_grey_500, null))
            "Blue Grey,600" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.blue_grey_600, null))
            "Blue Grey,700" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.blue_grey_700, null))
            "Blue Grey,800" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.blue_grey_800, null))
            "Blue Grey,900" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.blue_grey_900, null))

            "Pink,50" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.pink_50, null))
            "Pink,100" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.pink_100, null))
            "Pink,200" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.pink_200, null))
            "Pink,300" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.pink_300, null))
            "Pink,400" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.pink_400, null))
            "Pink,500" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.pink_500, null))
            "Pink,600" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.pink_600, null))
            "Pink,700" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.pink_700, null))
            "Pink,800" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.pink_800, null))
            "Pink,900" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.pink_900, null))
            "Pink,A100" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.pink_A100, null))
            "Pink,A200" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.pink_A200, null))
            "Pink,A400" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.pink_A400, null))
            "Pink,A700" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.pink_A700, null))

            "Indigo,50" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.indigo_50, null))
            "Indigo,100" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.indigo_100, null))
            "Indigo,200" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.indigo_200, null))
            "Indigo,300" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.indigo_300, null))
            "Indigo,400" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.indigo_400, null))
            "Indigo,500" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.indigo_500, null))
            "Indigo,600" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.indigo_600, null))
            "Indigo,700" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.indigo_700, null))
            "Indigo,800" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.indigo_800, null))
            "Indigo,900" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.indigo_900, null))
            "Indigo,A100" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.indigo_A100, null))
            "Indigo,A200" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.indigo_A200, null))
            "Indigo,A400" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.indigo_A400, null))
            "Indigo,A700" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.indigo_A700, null))

            "Cyan,50" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.cyan_50, null))
            "Cyan,100" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.cyan_100, null))
            "Cyan,200" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.cyan_200, null))
            "Cyan,300" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.cyan_300, null))
            "Cyan,400" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.cyan_400, null))
            "Cyan,500" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.cyan_500, null))
            "Cyan,600" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.cyan_600, null))
            "Cyan,700" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.cyan_700, null))
            "Cyan,800" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.cyan_800, null))
            "Cyan,900" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.cyan_900, null))
            "Cyan,A100" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.cyan_A100, null))
            "Cyan,A200" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.cyan_A200, null))
            "Cyan,A400" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.cyan_A400, null))
            "Cyan,A700" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.cyan_A700, null))

            "Light Green,50" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.light_green_50, null))
            "Light Green,100" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.light_green_100, null))
            "Light Green,200" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.light_green_200, null))
            "Light Green,300" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.light_green_300, null))
            "Light Green,400" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.light_green_400, null))
            "Light Green,500" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.light_green_500, null))
            "Light Green,600" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.light_green_600, null))
            "Light Green,700" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.light_green_700, null))
            "Light Green,800" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.light_green_800, null))
            "Light Green,900" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.light_green_900, null))
            "Light Green,A100" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.light_green_A100, null))
            "Light Green,A200" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.light_green_A200, null))
            "Light Green,A400" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.light_green_A400, null))
            "Light Green,A700" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.light_green_A700, null))

            "Amber,50" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.amber_50, null))
            "Amber,100" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.amber_100, null))
            "Amber,200" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.amber_200, null))
            "Amber,300" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.amber_300, null))
            "Amber,400" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.amber_400, null))
            "Amber,500" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.amber_500, null))
            "Amber,600" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.amber_600, null))
            "Amber,700" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.amber_700, null))
            "Amber,800" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.amber_800, null))
            "Amber,900" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.amber_900, null))
            "Amber,A100" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.amber_A100, null))
            "Amber,A200" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.amber_A200, null))
            "Amber,A400" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.amber_A400, null))
            "Amber,A700" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.amber_A700, null))

            "Brown,50" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.brown_50, null))
            "Brown,100" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.brown_100, null))
            "Brown,200" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.brown_200, null))
            "Brown,300" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.brown_300, null))
            "Brown,400" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.brown_400, null))
            "Brown,500" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.brown_500, null))
            "Brown,600" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.brown_600, null))
            "Brown,700" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.brown_700, null))
            "Brown,800" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.brown_800, null))
            "Brown,900" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.brown_900, null))

            "Purple,50" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.purple_50, null))
            "Purple,100" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.purple_100, null))
            "Purple,200" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.purple_200, null))
            "Purple,300" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.purple_300, null))
            "Purple,400" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.purple_400, null))
            "Purple,500" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.purple_500, null))
            "Purple,600" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.purple_600, null))
            "Purple,700" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.purple_700, null))
            "Purple,800" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.purple_800, null))
            "Purple,900" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.purple_900, null))
            "Purple,A100" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.purple_A100, null))
            "Purple,A200" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.purple_A200, null))
            "Purple,A400" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.purple_A400, null))
            "Purple,A700" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.purple_A700, null))

            "Blue,50" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.blue_50, null))
            "Blue,100" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.blue_100, null))
            "Blue,200" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.blue_200, null))
            "Blue,300" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.blue_300, null))
            "Blue,400" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.blue_400, null))
            "Blue,500" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.blue_500, null))
            "Blue,600" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.blue_600, null))
            "Blue,700" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.blue_700, null))
            "Blue,800" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.blue_800, null))
            "Blue,900" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.blue_900, null))
            "Blue,A100" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.blue_A100, null))
            "Blue,A200" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.blue_A200, null))
            "Blue,A400" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.blue_A400, null))
            "Blue,A700" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.blue_A700, null))

            "Teal,50" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.teal_50, null))
            "Teal,100" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.teal_100, null))
            "Teal,200" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.teal_200, null))
            "Teal,300" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.teal_300, null))
            "Teal,400" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.teal_400, null))
            "Teal,500" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.teal_500, null))
            "Teal,600" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.teal_600, null))
            "Teal,700" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.teal_700, null))
            "Teal,800" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.teal_800, null))
            "Teal,900" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.teal_900, null))
            "Teal,A100" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.teal_A100, null))
            "Teal,A200" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.teal_A200, null))
            "Teal,A400" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.teal_A400, null))
            "Teal,A700" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.teal_A700, null))

            "Lime,50" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.lime_50, null))
            "Lime,100" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.lime_100, null))
            "Lime,200" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.lime_200, null))
            "Lime,300" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.lime_300, null))
            "Lime,400" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.lime_400, null))
            "Lime,500" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.lime_500, null))
            "Lime,600" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.lime_600, null))
            "Lime,700" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.lime_700, null))
            "Lime,800" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.lime_800, null))
            "Lime,900" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.lime_900, null))
            "Lime,A100" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.lime_A100, null))
            "Lime,A200" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.lime_A200, null))
            "Lime,A400" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.lime_A400, null))
            "Lime,A700" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.lime_A700, null))

            "Orange,50" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.orange_50, null))
            "Orange,100" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.orange_100, null))
            "Orange,200" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.orange_200, null))
            "Orange,300" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.orange_300, null))
            "Orange,400" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.orange_400, null))
            "Orange,500" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.orange_500, null))
            "Orange,600" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.orange_600, null))
            "Orange,700" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.orange_700, null))
            "Orange,800" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.orange_800, null))
            "Orange,900" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.orange_900, null))
            "Orange,A100" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.orange_A100, null))
            "Orange,A200" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.orange_A200, null))
            "Orange,A400" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.orange_A400, null))
            "Orange,A700" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.orange_A700, null))

            "Grey,50" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.grey_50, null))
            "Grey,100" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.grey_100, null))
            "Grey,200" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.grey_200, null))
            "Grey,300" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.grey_300, null))
            "Grey,400" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.grey_400, null))
            "Grey,500" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.grey_500, null))
            "Grey,600" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.grey_600, null))
            "Grey,700" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.grey_700, null))
            "Grey,800" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.grey_800, null))
            "Grey,900" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.grey_900, null))

            "Black" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.black, null))
            "White" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.white, null))

            else -> ColorStateList.valueOf(App.context.resources.getColor(R.color.black, null))
        }

        fun getColorStateList(colourFamily: String, colourIntensity: String?) = when (colourFamily) {
            "Red" -> {
                when (colourIntensity) {
                    "50" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.red_50, null))
                    "100" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.red_100, null))
                    "200" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.red_200, null))
                    "300" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.red_300, null))
                    "400" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.red_400, null))
                    "500" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.red_500, null))
                    "600" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.red_600, null))
                    "700" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.red_700, null))
                    "800" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.red_800, null))
                    "900" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.red_900, null))
                    "A100" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.red_A100, null))
                    "A200" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.red_A200, null))
                    "A400" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.red_A400, null))
                    "A700" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.red_A700, null))
                    else -> ColorStateList.valueOf(App.context.resources.getColor(R.color.black, null))
                }
            }
            "Deep Purple" -> {
                when (colourIntensity) {
                    "50" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.deep_purple_50, null))
                    "100" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.deep_purple_100, null))
                    "200" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.deep_purple_200, null))
                    "300" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.deep_purple_300, null))
                    "400" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.deep_purple_400, null))
                    "500" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.deep_purple_500, null))
                    "600" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.deep_purple_600, null))
                    "700" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.deep_purple_700, null))
                    "800" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.deep_purple_800, null))
                    "900" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.deep_purple_900, null))
                    "A100" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.deep_purple_A100, null))
                    "A200" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.deep_purple_A200, null))
                    "A400" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.deep_purple_A400, null))
                    "A700" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.deep_purple_A700, null))
                    else -> ColorStateList.valueOf(App.context.resources.getColor(R.color.black, null))
                }
            }
            "Light Blue" -> {
                when (colourIntensity) {
                    "50" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.light_blue_50, null))
                    "100" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.light_blue_100, null))
                    "200" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.light_blue_200, null))
                    "300" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.light_blue_300, null))
                    "400" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.light_blue_400, null))
                    "500" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.light_blue_500, null))
                    "600" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.light_blue_600, null))
                    "700" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.light_blue_700, null))
                    "800" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.light_blue_800, null))
                    "900" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.light_blue_900, null))
                    "A100" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.light_blue_A100, null))
                    "A200" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.light_blue_A200, null))
                    "A400" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.light_blue_A400, null))
                    "A700" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.light_blue_A700, null))
                    else -> ColorStateList.valueOf(App.context.resources.getColor(R.color.black, null))
                }
            }
            "Green" -> {
                when (colourIntensity) {
                    "50" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.green_50, null))
                    "100" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.green_100, null))
                    "200" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.green_200, null))
                    "300" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.green_300, null))
                    "400" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.green_400, null))
                    "500" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.green_500, null))
                    "600" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.green_600, null))
                    "700" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.green_700, null))
                    "800" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.green_800, null))
                    "900" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.green_900, null))
                    "A100" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.green_A100, null))
                    "A200" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.green_A200, null))
                    "A400" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.green_A400, null))
                    "A700" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.green_A700, null))
                    else -> ColorStateList.valueOf(App.context.resources.getColor(R.color.black, null))
                }
            }
            "Yellow" -> {
                when (colourIntensity) {
                    "50" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.yellow_50, null))
                    "100" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.yellow_100, null))
                    "200" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.yellow_200, null))
                    "300" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.yellow_300, null))
                    "400" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.yellow_400, null))
                    "500" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.yellow_500, null))
                    "600" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.yellow_600, null))
                    "700" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.yellow_700, null))
                    "800" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.yellow_800, null))
                    "900" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.yellow_900, null))
                    "A100" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.yellow_A100, null))
                    "A200" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.yellow_A200, null))
                    "A400" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.yellow_A400, null))
                    "A700" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.yellow_A700, null))
                    else -> ColorStateList.valueOf(App.context.resources.getColor(R.color.black, null))
                }
            }
            "Deep Orange" -> {
                when (colourIntensity) {
                    "50" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.deep_orange_50, null))
                    "100" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.deep_orange_100, null))
                    "200" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.deep_orange_200, null))
                    "300" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.deep_orange_300, null))
                    "400" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.deep_orange_400, null))
                    "500" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.deep_orange_500, null))
                    "600" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.deep_orange_600, null))
                    "700" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.deep_orange_700, null))
                    "800" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.deep_orange_800, null))
                    "900" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.deep_orange_900, null))
                    "A100" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.deep_orange_A100, null))
                    "A200" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.deep_orange_A200, null))
                    "A400" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.deep_orange_A400, null))
                    "A700" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.deep_orange_A700, null))
                    else -> ColorStateList.valueOf(App.context.resources.getColor(R.color.black, null))
                }
            }
            "Blue Grey" -> {
                when (colourIntensity) {
                    "50" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.blue_grey_50, null))
                    "100" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.blue_grey_100, null))
                    "200" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.blue_grey_200, null))
                    "300" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.blue_grey_300, null))
                    "400" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.blue_grey_400, null))
                    "500" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.blue_grey_500, null))
                    "600" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.blue_grey_600, null))
                    "700" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.blue_grey_700, null))
                    "800" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.blue_grey_800, null))
                    "900" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.blue_grey_900, null))
                    else -> ColorStateList.valueOf(App.context.resources.getColor(R.color.black, null))
                }
            }
            "Pink" -> {
                when (colourIntensity) {
                    "50" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.pink_50, null))
                    "100" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.pink_100, null))
                    "200" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.pink_200, null))
                    "300" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.pink_300, null))
                    "400" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.pink_400, null))
                    "500" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.pink_500, null))
                    "600" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.pink_600, null))
                    "700" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.pink_700, null))
                    "800" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.pink_800, null))
                    "900" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.pink_900, null))
                    "A100" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.pink_A100, null))
                    "A200" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.pink_A200, null))
                    "A400" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.pink_A400, null))
                    "A700" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.pink_A700, null))
                    else -> ColorStateList.valueOf(App.context.resources.getColor(R.color.black, null))
                }
            }
            "Indigo" -> {
                when (colourIntensity) {
                    "50" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.indigo_50, null))
                    "100" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.indigo_100, null))
                    "200" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.indigo_200, null))
                    "300" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.indigo_300, null))
                    "400" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.indigo_400, null))
                    "500" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.indigo_500, null))
                    "600" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.indigo_600, null))
                    "700" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.indigo_700, null))
                    "800" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.indigo_800, null))
                    "900" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.indigo_900, null))
                    "A100" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.indigo_A100, null))
                    "A200" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.indigo_A200, null))
                    "A400" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.indigo_A400, null))
                    "A700" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.indigo_A700, null))
                    else -> ColorStateList.valueOf(App.context.resources.getColor(R.color.black, null))
                }
            }
            "Cyan" -> {
                when (colourIntensity) {
                    "50" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.cyan_50, null))
                    "100" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.cyan_100, null))
                    "200" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.cyan_200, null))
                    "300" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.cyan_300, null))
                    "400" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.cyan_400, null))
                    "500" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.cyan_500, null))
                    "600" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.cyan_600, null))
                    "700" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.cyan_700, null))
                    "800" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.cyan_800, null))
                    "900" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.cyan_900, null))
                    "A100" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.cyan_A100, null))
                    "A200" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.cyan_A200, null))
                    "A400" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.cyan_A400, null))
                    "A700" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.cyan_A700, null))
                    else -> ColorStateList.valueOf(App.context.resources.getColor(R.color.black, null))
                }
            }
            "Light Green" -> {
                when (colourIntensity) {
                    "50" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.light_green_50, null))
                    "100" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.light_green_100, null))
                    "200" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.light_green_200, null))
                    "300" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.light_green_300, null))
                    "400" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.light_green_400, null))
                    "500" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.light_green_500, null))
                    "600" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.light_green_600, null))
                    "700" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.light_green_700, null))
                    "800" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.light_green_800, null))
                    "900" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.light_green_900, null))
                    "A100" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.light_green_A100, null))
                    "A200" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.light_green_A200, null))
                    "A400" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.light_green_A400, null))
                    "A700" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.light_green_A700, null))
                    else -> ColorStateList.valueOf(App.context.resources.getColor(R.color.black, null))
                }
            }
            "Amber" -> {
                when (colourIntensity) {
                    "50" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.amber_50, null))
                    "100" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.amber_100, null))
                    "200" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.amber_200, null))
                    "300" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.amber_300, null))
                    "400" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.amber_400, null))
                    "500" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.amber_500, null))
                    "600" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.amber_600, null))
                    "700" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.amber_700, null))
                    "800" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.amber_800, null))
                    "900" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.amber_900, null))
                    "A100" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.amber_A100, null))
                    "A200" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.amber_A200, null))
                    "A400" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.amber_A400, null))
                    "A700" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.amber_A700, null))
                    else -> ColorStateList.valueOf(App.context.resources.getColor(R.color.black, null))
                }
            }
            "Brown" -> {
                when (colourIntensity) {
                    "50" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.brown_50, null))
                    "100" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.brown_100, null))
                    "200" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.brown_200, null))
                    "300" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.brown_300, null))
                    "400" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.brown_400, null))
                    "500" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.brown_500, null))
                    "600" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.brown_600, null))
                    "700" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.brown_700, null))
                    "800" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.brown_800, null))
                    "900" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.brown_900, null))
                    else -> ColorStateList.valueOf(App.context.resources.getColor(R.color.black, null))
                }
            }
            "Purple" -> {
                when (colourIntensity) {
                    "50" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.purple_50, null))
                    "100" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.purple_100, null))
                    "200" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.purple_200, null))
                    "300" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.purple_300, null))
                    "400" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.purple_400, null))
                    "500" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.purple_500, null))
                    "600" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.purple_600, null))
                    "700" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.purple_700, null))
                    "800" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.purple_800, null))
                    "900" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.purple_900, null))
                    "A100" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.purple_A100, null))
                    "A200" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.purple_A200, null))
                    "A400" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.purple_A400, null))
                    "A700" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.purple_A700, null))
                    else -> ColorStateList.valueOf(App.context.resources.getColor(R.color.black, null))
                }
            }
            "Blue" -> {
                when (colourIntensity) {
                    "50" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.blue_50, null))
                    "100" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.blue_100, null))
                    "200" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.blue_200, null))
                    "300" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.blue_300, null))
                    "400" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.blue_400, null))
                    "500" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.blue_500, null))
                    "600" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.blue_600, null))
                    "700" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.blue_700, null))
                    "800" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.blue_800, null))
                    "900" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.blue_900, null))
                    "A100" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.blue_A100, null))
                    "A200" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.blue_A200, null))
                    "A400" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.blue_A400, null))
                    "A700" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.blue_A700, null))
                    else -> ColorStateList.valueOf(App.context.resources.getColor(R.color.black, null))
                }
            }
            "Teal" -> {
                when (colourIntensity) {
                    "50" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.teal_50, null))
                    "100" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.teal_100, null))
                    "200" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.teal_200, null))
                    "300" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.teal_300, null))
                    "400" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.teal_400, null))
                    "500" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.teal_500, null))
                    "600" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.teal_600, null))
                    "700" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.teal_700, null))
                    "800" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.teal_800, null))
                    "900" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.teal_900, null))
                    "A100" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.teal_A100, null))
                    "A200" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.teal_A200, null))
                    "A400" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.teal_A400, null))
                    "A700" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.teal_A700, null))
                    else -> ColorStateList.valueOf(App.context.resources.getColor(R.color.black, null))
                }
            }
            "Lime" -> {
                when (colourIntensity) {
                    "50" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.lime_50, null))
                    "100" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.lime_100, null))
                    "200" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.lime_200, null))
                    "300" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.lime_300, null))
                    "400" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.lime_400, null))
                    "500" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.lime_500, null))
                    "600" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.lime_600, null))
                    "700" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.lime_700, null))
                    "800" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.lime_800, null))
                    "900" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.lime_900, null))
                    "A100" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.lime_A100, null))
                    "A200" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.lime_A200, null))
                    "A400" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.lime_A400, null))
                    "A700" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.lime_A700, null))
                    else -> ColorStateList.valueOf(App.context.resources.getColor(R.color.black, null))
                }
            }
            "Orange" -> {
                when (colourIntensity) {
                    "50" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.orange_50, null))
                    "100" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.orange_100, null))
                    "200" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.orange_200, null))
                    "300" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.orange_300, null))
                    "400" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.orange_400, null))
                    "500" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.orange_500, null))
                    "600" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.orange_600, null))
                    "700" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.orange_700, null))
                    "800" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.orange_800, null))
                    "900" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.orange_900, null))
                    "A100" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.orange_A100, null))
                    "A200" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.orange_A200, null))
                    "A400" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.orange_A400, null))
                    "A700" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.orange_A700, null))
                    else -> ColorStateList.valueOf(App.context.resources.getColor(R.color.black, null))
                }
            }
            "Grey" -> {
                when (colourIntensity) {
                    "50" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.grey_50, null))
                    "100" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.grey_100, null))
                    "200" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.grey_200, null))
                    "300" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.grey_300, null))
                    "400" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.grey_400, null))
                    "500" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.grey_500, null))
                    "600" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.grey_600, null))
                    "700" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.grey_700, null))
                    "800" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.grey_800, null))
                    "900" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.grey_900, null))
                    else -> ColorStateList.valueOf(App.context.resources.getColor(R.color.black, null))
                }
            }
            "Black" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.black, null))
            "White" -> ColorStateList.valueOf(App.context.resources.getColor(R.color.white, null))
            else -> ColorStateList.valueOf(App.context.resources.getColor(R.color.black, null))
        }
    }
}