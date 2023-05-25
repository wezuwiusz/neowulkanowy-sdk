# Oceny

Poniżej przedstawiamy przykład pobrania ocen przez nasze Sdk, od początkowego logowania, przez wybór ucznia aż do wyświetlenia listy wszystkich ocen z ostatniego semestru wybranego ucznia.

!!! warning
    Nie wykonuj za każdym razem wyszukiwania listy uczniów — trwa to długo i nie ma z tego żadnego pożytku.
    Zamiast tego zapisz gdzieś wybrany `symbol`, `schoolSymbol`, `studentId`, `diaryId` oraz `semesterId` i używaj tych zapisanych danych do późniejszego ustawiania Sdk.

```kotlin
// ustawiamy dane logowania
val userEmail = "jan@fakelog.cf"
val userPassword = "jan123"
val host = "https://fakelog.cf/"

// tworzymy obiekt Sdk i pobieramy listę dostępnych uczniów
val sdk = Sdk()
val registerUser: RegisterUser = sdk.getUserSubjectsFromScrapper(
    email = userEmail,
    password = userPassword,
    scrapperBaseUrl = host,
)

// na potrzeby tego demo odfiltrowujemy puste symbole i szkoły bez aktywnych uczniów 
val registerSymbol = registerUser.symbols
    .filter { it.schools.isNotEmpty() }
    .first { it.schools.all { school -> school.subjects.isNotEmpty() } }
val registerUnit = registerSymbol.schools.first()
val registerStudent = registerUnit.subjects.filterIsInstance<RegisterStudent>().first()
val semester = registerStudent.semesters.last()

// re-inicjalizujemy Sdk z wcześniej pozyskanymi danymi
sdk.apply {
    email = userEmail
    password = userPassword
    scrapperBaseUrl = host
    loginType = Sdk.ScrapperLoginType.valueOf(registerUser.loginType?.name!!) // todo: to akurat brzydkie, przeoczyłem to jakoś

    symbol = registerSymbol.symbol
    schoolSymbol = registerUnit.schoolId
    studentId = registerStudent.studentId
    diaryId = semester.diaryId
}

// pobieramy i wyświetlamy oceny z ostatniego semestru pierwszego dostępnego ucznia
val grades = sdk.getGrades(semester.semesterId)
grades.details.forEach {
    println("${it.entry} - ${it.date}")
}
```
