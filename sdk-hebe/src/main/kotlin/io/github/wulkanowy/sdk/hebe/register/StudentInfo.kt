package io.github.wulkanowy.sdk.hebe.register

import com.google.gson.annotations.SerializedName

class StudentInfo(

    @SerializedName("Capabilities")
    val capabilities: List<String>,

    @SerializedName("ClassDisplay")
    val classDisplay: String,

    @SerializedName("ConstituentUnit")
    val constituentUnit: ConstituentUnit,

    @SerializedName("Educators")
    val educators: List<Educator>,

    @SerializedName("FullSync")
    val fullSync: Boolean,

    @SerializedName("InfoDisplay")
    val infoDisplay: String,

    @SerializedName("Journal")
    val journal: Journal,

    @SerializedName("Login")
    val login: Login,

    @SerializedName("Partition")
    val partition: String,

    @SerializedName("Periods")
    val periods: List<Period>,

    @SerializedName("Pupil")
    val pupil: Pupil,

    @SerializedName("SenderEntry")
    val senderEntry: SenderEntry,

    @SerializedName("TopLevelPartition")
    val topLevelPartition: String,

    @SerializedName("Unit")
    val unit: Unit
) {

    data class ConstituentUnit(

        @SerializedName("Address")
        val address: String,

        @SerializedName("Id")
        val id: Int,

        @SerializedName("Name")
        val name: String,

        @SerializedName("Patron")
        val patron: String,

        @SerializedName("SchoolTopic")
        val schoolTopic: String,

        @SerializedName("Short")
        val short: String
    )

    data class Educator(

        @SerializedName("Id")
        val id: Int,

        @SerializedName("Initials")
        val initials: String,

        @SerializedName("LoginId")
        val loginId: Int,

        @SerializedName("Name")
        val name: String,

        @SerializedName("Surname")
        val surname: String,

        @SerializedName("Roles")
        val roles: List<Role>
    ) {
        data class Role(

            @SerializedName("Address")
            val address: String,

            @SerializedName("AddressHash")
            val addressHash: String,

            @SerializedName("ClassSymbol")
            val classSymbol: String,

            @SerializedName("ConstituentUnitSymbol")
            val constituentUnitSymbol: String,

            @SerializedName("Initials")
            val initials: String,

            @SerializedName("Name")
            val name: String,

            @SerializedName("RoleName")
            val roleName: String,

            @SerializedName("RoleOrder")
            val roleOrder: Int,

            @SerializedName("Surname")
            val surname: String,

            @SerializedName("UnitSymbol")
            val unitSymbol: String?
        )
    }

    data class Journal(

        @SerializedName("Id")
        val id: Int,

        @SerializedName("YearStart")
        val yearStart: PeriodDate,

        @SerializedName("YearEnd")
        val yearEnd: PeriodDate
    )

    data class PeriodDate(

        @SerializedName("Date")
        val date: String,

        @SerializedName("DateDisplay")
        val dateDisplay: String,

        @SerializedName("Time")
        val time: String,

        @SerializedName("Timestamp")
        val timestamp: Long
    )

    data class Login(

        @SerializedName("DisplayName")
        val displayName: String,

        @SerializedName("FirstName")
        val firstName: String,

        @SerializedName("Id")
        val id: Int,

        @SerializedName("LoginRole")
        val loginRole: String,

        @SerializedName("SecondName")
        val secondName: String,

        @SerializedName("Surname")
        val surname: String,

        @SerializedName("Value")
        val value: String
    )

    data class Period(

        @SerializedName("Current")
        val current: Boolean,

        @SerializedName("End")
        val end: PeriodDate,

        @SerializedName("Id")
        val id: Int,

        @SerializedName("Last")
        val last: Boolean,

        @SerializedName("Level")
        val level: Int,

        @SerializedName("Number")
        val number: Int,

        @SerializedName("Start")
        val start: PeriodDate
    )

    data class Pupil(

        @SerializedName("FirstName")
        val firstName: String,

        @SerializedName("Id")
        val id: Int,

        @SerializedName("LoginId")
        val loginId: Int,

        @SerializedName("LoginValue")
        val loginValue: String,

        @SerializedName("SecondName")
        val secondName: String,

        @SerializedName("Sex")
        val sex: Boolean,

        @SerializedName("Surname")
        val surname: String
    )

    data class SenderEntry(

        @SerializedName("Address")
        val address: String,

        @SerializedName("AddressHash")
        val addressHash: String,

        @SerializedName("Initials")
        val initials: String,

        @SerializedName("LoginId")
        val loginId: Int
    )

    data class Unit(

        @SerializedName("Address")
        val address: String,

        @SerializedName("DisplayName")
        val displayName: String,

        @SerializedName("Id")
        val id: Int,

        @SerializedName("Name")
        val name: String,

        @SerializedName("Patron")
        val patron: String,

        @SerializedName("RestURL")
        val restUrl: String,

        @SerializedName("Short")
        val short: String,

        @SerializedName("Symbol")
        val symbol: String
    )
}
