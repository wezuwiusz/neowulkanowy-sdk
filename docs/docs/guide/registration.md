# Rejestracja

Zależnie od wybranego trybu "rejestracja" zachowuje się inaczej, lecz ostatecznie jej wynikiem jest zwrócenie listy uczniów podpiętych do konta.
I tak dla trybu:

* SCRAPPER — SDK spróbuje wyszukać na ostatnich 10 symbolach, na które logował się użytkownik, wszystkich dostępnych uczniów 
* HEBE — zarejestrowane zostanie urządzenie przez "Mobilny dostęp" i zwróceni zostaną uczniowie z tylko z danego symbolu
* HYBRID — połączenie obu metod — SDK zarejestruje urządzenie dla każdego z symboli

!!! note
    Każda z tych metod zwraca tę samą strukturę danych, czyli `RegisterUser`, w której znajdują się wszystkie potrzebne dane, by używać reszty SDK.

## Tryby działania

### SCRAPPER

Scrapper obsługuje wszystkie obecnie znane strony logowania do różnych _odmian_ dziennika UONET+.
W niektórych odmianach zamiast emaila stosuje się przyznany login (np. JANKOWA12).

Opis parametrów:

* `email` — adres e-mail użytkownika w przypadku odmiany vulcan.net.pl, w przypadku innych przydzielony login
* `password` — hasło użytkownika
* `scrapperBaseUrl` — adres bazowy odmiany dziennika. W większości przypadków będzie to `https://vulcan.net.pl/`, ale mogą być też inne, np. `https://opolska.eszkola.pl/`
* `symbol` — opcjonalne (domyślnie `Default`). Symbol, od którego SDK będzie rozpoczynało przeszukiwanie dziennika.

!!! warning
    Jeśli używasz innej odmiany niż `vulcan.net.pl` to zawsze podawaj konkretny symbol, inaczej SDK prawdopodobnie niczego nie znajdzie.  

```kotlin
import io.github.wulkanowy.sdk.Sdk

val sdk = Sdk()
val registerUser: RegisterUser = sdk.getUserSubjectsFromScrapper(
    email = "jan@fakelog.cf",
    password = "jan123",
    scrapperBaseUrl = "https://fakelog.cf/",
    symbol = "powiatwulkanowy",
)
```


### HEBE

Hebe, dzięki swojej koncepcji wspiera i póki będzie istnieć, to będzie wspierać wszystkie odmiany dziennika.
Z tego powodu wygrywa z trybem SCRAPPER pod względem stabilności i odporności na możliwe zmiany.

!!! warning
    Obsługa trybu HEBE jest w tej chwili mocno wybrakowana.

By zdobyć token, pin i symbol musisz ręcznie [zarejestrować urządzenie](https://wulkanowy.github.io/czesto-zadawane-pytania/co-to-jest-symbol).

`FirebaseToken` jest opcjonalnym parametrem, gdzie można przekazać... firebase token, używany do wysyłania powiadomień.

```kotlin
import io.github.wulkanowy.sdk.Sdk

val sdk = Sdk()
val registerUser: RegisterUser = sdk.getStudentsFromHebe(
    token = "FK100000",
    pin = "999999",
    symbol = "powiatwulkanowy",
    firebaseToken = null,
)
```


### HYBRID

Tryb hybrydowy to specjalny tryb, który pod spodem używa dwóch powyższych metod, tj. najpierw wyszukuje wszystkich uczniów przez scrapper, a następnie rejestruje dla każdego znalezionego symbolu po jednym urządzeniu mobilnym.
Z tego powodu nie ma potrzeby ręcznego przekazywania tokenu i pinu, bo SDK zdobędzie je samodzielnie.

```kotlin
import io.github.wulkanowy.sdk.Sdk

val sdk = Sdk()
val registerUser: RegisterUser = sdk.getStudentsHybrid(
    email = "jan@fakelog.cf",
    password = "jan123",
    scrapperBaseUrl = "https://fakelog.cf/",
    startSymbol = "powiatwulkanowy",
    firebaseToken = null,
)
```

## Struktura RegisterUser

`RegisterUser` ma budowę _hierarchiczną_, dzięki czemu odzwierciedla faktyczne zależności między kolejnymi poziomami dostępu do danych.

Rzeczona hierarchia wygląda następująco:

```
RegisterUser
    RegisterSymbol
        RegisterUnit
            RegisterSubject
                - RegisterEmployee
                - RegisterStudent
                    Semester
```

### RegisterUser

Na szczycie tej hierarchii jest użytkownik, w którym dołączone podstawowe informacje o koncie, tj. użyty e-mail, faktyczny login, typ logowania (którego ma używać później SDK), wybrany tryb działania SDK, jak i symbole (`RegisterSymbol`). 

### RegisterSymbol

Każdy użytkownik dziennika może mieć dostęp do danych na wielu symbolach (np. rodzic, którego jedno dziecko uczy się w szkole podstawowej w mieście powiatowym, a drugie dziecko w szkole średniej w tym samym mieście).

Oprócz informacji o samym symbolu znajduje się tutaj informacja o możliwym błędzie przy próbie dostępu (jeśli takowy wystąpił), imię i nazwisko użytkownika, dane wykorzystywane przez HEBE (tj. `keyId`, `privatePem` i `hebeBaseUrl`) oraz listę szkół, znajdujących się pod danym symbolem, a do których dostęp ma dany użytkownik.  

### RegisterUnit

Każda szkoła (jednostka — `unit`) ma swój identyfikator (`schoolId`), nazwę długą i krótką (`schoolName` i `schoolShortName`), trochę idków różnych typów użytkowników, podobnie jak w `RegisterSymbol` pole na możliwe błędy oraz listę _subjectów_ (podmiotów?), którymi mogą być uczeń lub pracownik.

### RegisterSubject

`RegisterSubject` to interfejs, który ma dwie implementacje: `RegisterEmployee` oraz `RegisterStudent`.

Pierwszej można użyć, do pobrania informacji o pracowniku (np. nauczycielu), by móc dalej używać SDK np. do obsługi wiadomości dla takiego użytkownika.

Druga to uczeń, który oprócz takich podstawowych informacji jak imiona, nazwa klasy, id ucznia oraz klasy posiada również listę semestrów.
