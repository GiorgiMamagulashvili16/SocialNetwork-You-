package com.example.you.models.covid

data class CovidStatistics(
    val active: Number,
    val affectedCountries: Number,
    val cases: Number,
    val casesPerOneMillion: Number,
    val critical: Number,
    val deaths: Number,
    val deathsPerOneMillion: Number,
    val recovered: Number,
    val tests: Number,
    val testsPerOneMillion: Number,
    val todayCases: Number,
    val todayDeaths: Number,
    val updated: Number
)