package io.github.wulkanowy.sdk.scrapper

internal object ApiEndpoints : IApiEndpoints {

    var currentVersion = 58666

    private val endpoints
        get() = when (currentVersion) {
            in 58666..Int.MAX_VALUE -> ApiEndpoints_24_4_2_58666
            else -> ApiEndpoints_24_4_1_58566
        }

    // uczen
    override val Autoryzacja = endpoints.Autoryzacja
    override val DostepOffice = endpoints.DostepOffice
    override val EgzaminySemestralne = endpoints.EgzaminySemestralne
    override val EgzaminyZewnetrzne = endpoints.EgzaminyZewnetrzne
    override val EwidencjaObecnosci = endpoints.EwidencjaObecnosci
    override val FormularzeSzablony = endpoints.FormularzeSzablony
    override val FormularzeSzablonyDownload = endpoints.FormularzeSzablonyDownload
    override val FormularzeWysylanie = endpoints.FormularzeWysylanie
    override val Frekwencja = endpoints.Frekwencja
    override val FrekwencjaStatystyki = endpoints.FrekwencjaStatystyki
    override val FrekwencjaStatystykiPrzedmioty = endpoints.FrekwencjaStatystykiPrzedmioty
    override val Homework = endpoints.Homework
    override val Jadlospis = endpoints.Jadlospis
    override val LekcjeZaplanowane = endpoints.LekcjeZaplanowane
    override val LekcjeZrealizowane = endpoints.LekcjeZrealizowane
    override val Oceny = endpoints.Oceny
    override val Ogloszenia = endpoints.Ogloszenia
    override val Oplaty = endpoints.Oplaty
    override val PlanZajec = endpoints.PlanZajec
    override val Platnosc = endpoints.Platnosc
    override val PlatnoscMetadata = endpoints.PlatnoscMetadata
    override val PodrecznikiLataSzkolne = endpoints.PodrecznikiLataSzkolne
    override val PodrecznikiUcznia = endpoints.PodrecznikiUcznia
    override val Pomoc = endpoints.Pomoc
    override val RejestracjaUrzadzeniaToken = endpoints.RejestracjaUrzadzeniaToken
    override val RejestracjaUrzadzeniaTokenCertyfikat = endpoints.RejestracjaUrzadzeniaTokenCertyfikat
    override val RozpoczeciePlatnosci = endpoints.RozpoczeciePlatnosci
    override val ScalanieKont = endpoints.ScalanieKont
    override val Sprawdziany = endpoints.Sprawdziany
    override val Statystyki = endpoints.Statystyki
    override val SzkolaINauczyciele = endpoints.SzkolaINauczyciele
    override val Uczen = endpoints.Uczen
    override val UczenCache = endpoints.UczenCache
    override val UczenDziennik = endpoints.UczenDziennik
    override val UczenZdjecie = endpoints.UczenZdjecie
    override val Usprawiedliwienia = endpoints.Usprawiedliwienia
    override val UwagiIOsiagniecia = endpoints.UwagiIOsiagniecia
    override val ZarejestrowaneUrzadzenia = endpoints.ZarejestrowaneUrzadzenia
    override val Zebrania = endpoints.Zebrania
    override val ZebraniaObecnosc = endpoints.ZebraniaObecnosc
    override val ZgloszoneNieobecnosci = endpoints.ZgloszoneNieobecnosci

    // wiadomosciplus
    override val Skrzynki = endpoints.Skrzynki
    override val Odebrane = endpoints.Odebrane
    override val OdebraneSkrzynka = endpoints.OdebraneSkrzynka
    override val Wyslane = endpoints.Wyslane
    override val WyslaneSkrzynka = endpoints.WyslaneSkrzynka
    override val Usuniete = endpoints.Usuniete
    override val UsunieteSkrzynka = endpoints.UsunieteSkrzynka
    override val WiadomoscOdpowiedzPrzekaz = endpoints.WiadomoscOdpowiedzPrzekaz
    override val MoveTrash = endpoints.MoveTrash
    override val RestoreTrash = endpoints.RestoreTrash
}

internal interface IApiEndpoints {
    // uczen
    val Autoryzacja: String
    val DostepOffice: String
    val EgzaminySemestralne: String
    val EgzaminyZewnetrzne: String
    val EwidencjaObecnosci: String
    val FormularzeSzablony: String
    val FormularzeSzablonyDownload: String
    val FormularzeWysylanie: String
    val Frekwencja: String
    val FrekwencjaStatystyki: String
    val FrekwencjaStatystykiPrzedmioty: String
    val Homework: String
    val Jadlospis: String
    val LekcjeZaplanowane: String
    val LekcjeZrealizowane: String
    val Oceny: String
    val Ogloszenia: String
    val Oplaty: String
    val PlanZajec: String
    val Platnosc: String
    val PlatnoscMetadata: String
    val PodrecznikiLataSzkolne: String
    val PodrecznikiUcznia: String
    val Pomoc: String
    val RejestracjaUrzadzeniaToken: String
    val RejestracjaUrzadzeniaTokenCertyfikat: String
    val RozpoczeciePlatnosci: String
    val ScalanieKont: String
    val Sprawdziany: String
    val Statystyki: String
    val SzkolaINauczyciele: String
    val Uczen: String
    val UczenCache: String
    val UczenDziennik: String
    val UczenZdjecie: String
    val Usprawiedliwienia: String
    val UwagiIOsiagniecia: String
    val ZarejestrowaneUrzadzenia: String
    val Zebrania: String
    val ZebraniaObecnosc: String
    val ZgloszoneNieobecnosci: String

    // wiadomosciplus
    val Skrzynki: String
    val Odebrane: String
    val OdebraneSkrzynka: String
    val Wyslane: String
    val WyslaneSkrzynka: String
    val Usuniete: String
    val UsunieteSkrzynka: String
    val WiadomoscOdpowiedzPrzekaz: String
    val MoveTrash: String
    val RestoreTrash: String
}

internal object ApiEndpoints_24_4_1_58566 : IApiEndpoints {
    // uczen
    override val Autoryzacja = "Autoryzacja"
    override val DostepOffice = "DostepOffice"
    override val EgzaminySemestralne = "EgzaminySemestralne"
    override val EgzaminyZewnetrzne = "EgzaminyZewnetrzne"
    override val EwidencjaObecnosci = "EwidencjaObecnosci"
    override val FormularzeSzablony = "FormularzeSzablony"
    override val FormularzeSzablonyDownload = "FormularzeSzablonyDownload"
    override val FormularzeWysylanie = "FormularzeWysylanie"
    override val Frekwencja = "Frekwencja"
    override val FrekwencjaStatystyki = "FrekwencjaStatystyki"
    override val FrekwencjaStatystykiPrzedmioty = "FrekwencjaStatystykiPrzedmioty"
    override val Homework = "Homework"
    override val Jadlospis = "Jadlospis"
    override val LekcjeZaplanowane = "LekcjeZaplanowane"
    override val LekcjeZrealizowane = "LekcjeZrealizowane"
    override val Oceny = "Oceny"
    override val Ogloszenia = "Ogloszenia"
    override val Oplaty = "Oplaty"
    override val PlanZajec = "PlanZajec"
    override val Platnosc = "Platnosc"
    override val PlatnoscMetadata = "PlatnoscMetadata"
    override val PodrecznikiLataSzkolne = "PodrecznikiLataSzkolne"
    override val PodrecznikiUcznia = "PodrecznikiUcznia"
    override val Pomoc = "Pomoc"
    override val RejestracjaUrzadzeniaToken = "RejestracjaUrzadzeniaToken"
    override val RejestracjaUrzadzeniaTokenCertyfikat = "RejestracjaUrzadzeniaTokenCertyfikat"
    override val RozpoczeciePlatnosci = "RozpoczeciePlatnosci"
    override val ScalanieKont = "ScalanieKont"
    override val Sprawdziany = "Sprawdziany"
    override val Statystyki = "Statystyki"
    override val SzkolaINauczyciele = "SzkolaINauczyciele"
    override val Uczen = "Uczen"
    override val UczenCache = "UczenCache"
    override val UczenDziennik = "UczenDziennik"
    override val UczenZdjecie = "UczenZdjecie"
    override val Usprawiedliwienia = "Usprawiedliwienia"
    override val UwagiIOsiagniecia = "UwagiIOsiagniecia"
    override val ZarejestrowaneUrzadzenia = "ZarejestrowaneUrzadzenia"
    override val Zebrania = "Zebrania"
    override val ZebraniaObecnosc = "ZebraniaObecnosc"
    override val ZgloszoneNieobecnosci = "ZgloszoneNieobecnosci"

    // wiadomosciplus
    override val Skrzynki = "Skrzynki"
    override val Odebrane = "Odebrane"
    override val OdebraneSkrzynka = "OdebraneSkrzynka"
    override val Wyslane = "Wyslane"
    override val WyslaneSkrzynka = "WyslaneSkrzynka"
    override val Usuniete = "Usuniete"
    override val UsunieteSkrzynka = "UsunieteSkrzynka"
    override val WiadomoscOdpowiedzPrzekaz = "WiadomoscOdpowiedzPrzekaz"
    override val MoveTrash = "MoveTrash"
    override val RestoreTrash = "RestoreTrash"
}

internal object ApiEndpoints_24_4_2_58666 : IApiEndpoints {
    // uczen
    override val Autoryzacja = "00b61915-f3ec-421d-b7af-07aa8c4c162f"
    override val DostepOffice = "efa4a7ac-27bf-49af-90ff-ae0852339dd3"
    override val EgzaminySemestralne = "32915d4e-e542-4756-8c4b-c600f674f488"
    override val EgzaminyZewnetrzne = "9b48403a-cc4f-46a5-a51b-f04fea8753ee"
    override val EwidencjaObecnosci = "4ae4c4f5-209e-40e0-80db-3d77c484d528"
    override val FormularzeSzablony = "e36648d3-83fa-491b-8e91-5bea927120f1"
    override val FormularzeSzablonyDownload = "e1a812dd-9399-4354-aa5e-77304e042602"
    override val FormularzeWysylanie = "aa2d4eca-517c-4b53-ac44-8d5ea315bb7f"
    override val Frekwencja = "cc65835b-721d-4f4b-a316-799aed29c56f"
    override val FrekwencjaStatystyki = "a3ebd829-ee89-41c5-81c3-35a812c98640"
    override val FrekwencjaStatystykiPrzedmioty = "4c4133e2-cac7-444e-ab26-83501e5ddce5"
    override val Homework = "1dfb0cc9-584c-46c9-8f8b-bad061f9ebee"
    override val Jadlospis = "0c746ff5-f4cb-4537-b269-e3a4ac215eea"
    override val LekcjeZaplanowane = "3e8440cc-c832-45d9-abb4-3046977adb14"
    override val LekcjeZrealizowane = "9155e959-b2e7-4e05-9f2b-4773916b6dc2"
    override val Oceny = "42fd0eae-afaa-4772-ab73-66cdc1577649"
    override val Ogloszenia = "7e92f621-b90d-462e-ba6c-8b0110eb6494"
    override val Oplaty = "590bff89-a3ed-4bae-b0df-a18bdb1614e3"
    override val PlanZajec = "f6495353-4047-4752-91b6-ca202990e9e0"
    override val Platnosc = "4ba184c1-4419-4423-8864-8a6d7d1261c3"
    override val PlatnoscMetadata = "65c91912-8925-4bf2-b150-504324d781b8"
    override val PodrecznikiLataSzkolne = "862000c9-06a1-4505-b730-7fc3ed194007"
    override val PodrecznikiUcznia = "f65af6b8-1d86-4a47-9324-2c941eeb0140"
    override val Pomoc = "3380c914-a20d-44cd-9480-b2a64a3b5cf7"
    override val RejestracjaUrzadzeniaToken = "f51b7f8e-27fc-4522-aa87-fa881ee3afb8"
    override val RejestracjaUrzadzeniaTokenCertyfikat = "9fd15a23-2ec0-48be-8596-c46b7c30daac"
    override val RozpoczeciePlatnosci = "cd025374-b202-4efe-996c-18e074083775"
    override val ScalanieKont = "e4ac68c9-cf32-4ff6-be19-677cb5e5c255"
    override val Sprawdziany = "9bf0410c-aae5-447c-947e-49c815edbcb3"
    override val Statystyki = "dfa2d512-6682-414b-908b-6e208dfa0c6e"
    override val SzkolaINauczyciele = "b59d07c6-1146-4a50-ba6b-763ee8b151a3"
    override val Uczen = "a465bde1-9509-4706-bad2-658578482e04"
    override val UczenCache = "f18ddca0-400e-47cc-89a1-4bbe6685810b"
    override val UczenDziennik = "bef3daf1-07cd-4de6-b059-ee5909a7beb9"
    override val UczenZdjecie = "c1ba3b01-d53c-4577-98ab-4f1a620a3a35"
    override val Usprawiedliwienia = "863af74c-79aa-48f4-a0e6-aad168720dfd"
    override val UwagiIOsiagniecia = "6d869250-f637-4da0-a44b-c981299a55ce"
    override val ZarejestrowaneUrzadzenia = "bc9c2af4-fb13-4f9b-86e4-59ee12bfff85"
    override val Zebrania = "13fc67ca-19fc-4128-b8f0-41eed7f29838"
    override val ZebraniaObecnosc = "6ef8e1de-1234-4f39-9cd4-f68dabb8a440"
    override val ZgloszoneNieobecnosci = "80fe9dc2-8c36-41ea-8009-82503ff3c810"

    // wiadomosciplus
    override val Skrzynki = "787817a4-bfa8-472c-b1ce-bbe324b308cf"
    override val Odebrane = "fa8d0cb9-742e-4f9f-b3e2-65b7d5cd7aa6"
    override val OdebraneSkrzynka = "9545648d-6534-4282-acb7-6e9cc52ab402"
    override val Wyslane = "962f3256-b82a-42bd-8f20-f03d79723516"
    override val WyslaneSkrzynka = "a046f45d-42ae-4072-9684-e957a797a22b"
    override val Usuniete = "94c2e642-b8e3-41f7-8428-27c3f213cb5d"
    override val UsunieteSkrzynka = "bae7f54c-f18e-4553-92d6-9a5f93392b39"
    override val WiadomoscOdpowiedzPrzekaz = "44444524-ba04-4259-8853-88343cf294be"
    override val MoveTrash = "b2131c71-8d76-4d93-9274-1d5b4f30c915"
    override val RestoreTrash = "e6de58f9-4db1-4cd2-afdf-ac9fcca37e43"
}
