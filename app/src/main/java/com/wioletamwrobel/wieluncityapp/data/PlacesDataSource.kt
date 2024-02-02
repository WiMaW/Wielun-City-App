package com.wioletamwrobel.wieluncityapp.data

import com.wioletamwrobel.wieluncityapp.R
import com.wioletamwrobel.wieluncityapp.model.Place

object PlacesDataSource {

    val placeList: List<Place> = listOf(
        Place(
            R.string.city_hall,
            R.string.city_hall_description,
            R.string.city_hall_localization,
            R.drawable.city_hall,
            R.drawable.city_hall_small,
            R.string.history,
            "geo: 51.220479, 18.572694"
        ),
        Place(
            R.string.city_square,
            R.string.city_square_description,
            R.string.city_square_localization,
            R.drawable.plac_legionow,
            R.drawable.plac_legionow_small,
            R.string.entertainment,
            "geo: 51.220557, 18.570109"
        ),
        Place(
            R.string.former_castle,
            R.string.former_castle_description,
            R.string.former_castle_localization,
            R.drawable.former_castle,
            R.drawable.former_castle_small,
            R.string.history,
            "geo: 51.219580, 18.572042"
        ),
        Place(
            R.string.eternal_love_statue,
            R.string.eternal_love_statue_description,
            R.string.eternal_love_statue_localization,
            R.drawable.eternal_love_statue,
            R.drawable.eternal_love_statue_small,
            R.string.art,
            "geo: 51.220142, 18.572191"
        ),
        Place(
            R.string.saint_michael_church,
            R.string.saint_michael_church_description,
            R.string.saint_michael_church_localization,
            R.drawable.saint_michael_church,
            R.drawable.saint_michael_church_small,
            R.string.history,
            "geo: 51.220004, 18.570964"
        ),
        Place(
            R.string.defensive_walls,
            R.string.defensive_walls_description,
            R.string.defensive_walls_localization,
            R.drawable.defensive_walls,
            R.drawable.defensive_walls_small,
            R.string.history,
            "geo: 51.221088, 18.573351"
        ),
        Place(
            R.string.city_park,
            R.string.city_park_description,
            R.string.city_park_localization,
            R.drawable.park_center,
            R.drawable.park_center_small,
            R.string.nature,
            "geo: 51.218106, 18.573711"
        ),
        Place(
            R.string.museum,
            R.string.museum_description,
            R.string.museum_localization,
            R.drawable.museum,
            R.drawable.museum_small,
            R.string.history,
            "geo: 51.220327, 18.567308"
        ),
        Place(
            R.string.kaliska_street,
            R.string.kaliska_street_description,
            R.string.kaliska_street_localization,
            R.drawable.kaliska_street,
            R.drawable.kaliska_street_small,
            R.string.shops,
            "geo: 51.221257, 18.564623"
        ),
        Place(
            R.string.kaliska_gate,
            R.string.kaliska_gate_description,
            R.string.kaliska_gate_localization,
            R.drawable.kaliska_gate,
            R.drawable.kaliska_gate_small,
            R.string.history,
            "geo: 51.220935, 18.566833"
        ),
        Place(
            R.string.library,
            R.string.library_description,
            R.string.library_localization,
            R.drawable.library,
            R.drawable.library_small,
            R.string.entertainment,
            "geo: 51.220797, 18.567925"
        ),
        Place(
            R.string.cinema,
            R.string.cinema_description,
            R.string.cinema_localization,
            R.drawable.cinema,
            R.drawable.cinema_small,
            R.string.entertainment,
            "geo: 51.220595, 18.568488"
        ),
        Place(
            R.string.park,
            R.string.park_description,
            R.string.park_localization,
            R.drawable.park,
            R.drawable.park_small,
            R.string.nature,
            "geo: 51.221523, 18.554638"
        ),
        Place(
            R.string.jewish_cemetery,
            R.string.jewish_cemetery_description,
            R.string.jewish_cemetery_localization,
            R.drawable.cementary,
            R.drawable.cementary_small,
            R.string.history,
            "geo: 51.213356, 18.553898"
        )
    )

    val defaultPlace = placeList[0]
}