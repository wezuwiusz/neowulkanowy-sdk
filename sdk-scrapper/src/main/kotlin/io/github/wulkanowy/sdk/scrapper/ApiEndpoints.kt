package io.github.wulkanowy.sdk.scrapper

internal object ApiEndpoints : IApiEndpoints {

    var currentVersion = 58698

    private val endpoints
        get() = when (currentVersion) {
            58698 -> ApiEndpoints_24_04_0003_58698
            in 58666..58697 -> ApiEndpoints_24_4_2_58666
            else -> ApiEndpoints_24_4_1_58566
        }

    // uczen
    override val Autoryzacja get() = endpoints.Autoryzacja
    override val DostepOffice get() = endpoints.DostepOffice
    override val EgzaminySemestralne get() = endpoints.EgzaminySemestralne
    override val EgzaminyZewnetrzne get() = endpoints.EgzaminyZewnetrzne
    override val EwidencjaObecnosci get() = endpoints.EwidencjaObecnosci
    override val FormularzeSzablony get() = endpoints.FormularzeSzablony
    override val FormularzeSzablonyDownload get() = endpoints.FormularzeSzablonyDownload
    override val FormularzeWysylanie get() = endpoints.FormularzeWysylanie
    override val Frekwencja get() = endpoints.Frekwencja
    override val FrekwencjaStatystyki get() = endpoints.FrekwencjaStatystyki
    override val FrekwencjaStatystykiPrzedmioty get() = endpoints.FrekwencjaStatystykiPrzedmioty
    override val Homework get() = endpoints.Homework
    override val Jadlospis get() = endpoints.Jadlospis
    override val LekcjeZaplanowane get() = endpoints.LekcjeZaplanowane
    override val LekcjeZrealizowane get() = endpoints.LekcjeZrealizowane
    override val Oceny get() = endpoints.Oceny
    override val Ogloszenia get() = endpoints.Ogloszenia
    override val Oplaty get() = endpoints.Oplaty
    override val PlanZajec get() = endpoints.PlanZajec
    override val Platnosc get() = endpoints.Platnosc
    override val PlatnoscMetadata get() = endpoints.PlatnoscMetadata
    override val PodrecznikiLataSzkolne get() = endpoints.PodrecznikiLataSzkolne
    override val PodrecznikiUcznia get() = endpoints.PodrecznikiUcznia
    override val Pomoc get() = endpoints.Pomoc
    override val RejestracjaUrzadzeniaToken get() = endpoints.RejestracjaUrzadzeniaToken
    override val RejestracjaUrzadzeniaTokenCertyfikat get() = endpoints.RejestracjaUrzadzeniaTokenCertyfikat
    override val RozpoczeciePlatnosci get() = endpoints.RozpoczeciePlatnosci
    override val ScalanieKont get() = endpoints.ScalanieKont
    override val Sprawdziany get() = endpoints.Sprawdziany
    override val Statystyki get() = endpoints.Statystyki
    override val SzkolaINauczyciele get() = endpoints.SzkolaINauczyciele
    override val Uczen get() = endpoints.Uczen
    override val UczenCache get() = endpoints.UczenCache
    override val UczenDziennik get() = endpoints.UczenDziennik
    override val UczenZdjecie get() = endpoints.UczenZdjecie
    override val Usprawiedliwienia get() = endpoints.Usprawiedliwienia
    override val UwagiIOsiagniecia get() = endpoints.UwagiIOsiagniecia
    override val ZarejestrowaneUrzadzenia get() = endpoints.ZarejestrowaneUrzadzenia
    override val Zebrania get() = endpoints.Zebrania
    override val ZebraniaObecnosc get() = endpoints.ZebraniaObecnosc
    override val ZgloszoneNieobecnosci get() = endpoints.ZgloszoneNieobecnosci

    // uczenplus
    override val PlusContext get() = endpoints.PlusContext
    override val PlusAutoryzacjaPesel get() = endpoints.PlusAutoryzacjaPesel
    override val PlusFrekwencja get() = endpoints.PlusFrekwencja
    override val PlusUsprawiedliwienia get() = endpoints.PlusUsprawiedliwienia
    override val PlusFrekwencjaStatystyki get() = endpoints.PlusFrekwencjaStatystyki
    override val PlusZarejestrowaneUrzadzenia get() = endpoints.PlusZarejestrowaneUrzadzenia
    override val PlusRejestracjaUrzadzeniaToken get() = endpoints.PlusRejestracjaUrzadzeniaToken
    override val PlusZebrania get() = endpoints.PlusZebrania
    override val PlusRealizacjaZajec get() = endpoints.PlusRealizacjaZajec
    override val PlusSprawdzianyZadaniaDomowe get() = endpoints.PlusSprawdzianyZadaniaDomowe
    override val PlusSprawdzianSzczegoly get() = endpoints.PlusSprawdzianSzczegoly
    override val PlusZadanieDomoweSzczegoly get() = endpoints.PlusZadanieDomoweSzczegoly
    override val PlusPlanZajec get() = endpoints.PlusPlanZajec
    override val PlusDniWolne get() = endpoints.PlusDniWolne
    override val PlusUwagi get() = endpoints.PlusUwagi
    override val PlusNauczyciele get() = endpoints.PlusNauczyciele
    override val PlusInformacje get() = endpoints.PlusInformacje
    override val PlusDaneUcznia get() = endpoints.PlusDaneUcznia
    override val PlusUczenZdjecie get() = endpoints.PlusUczenZdjecie
    override val PlusOkresyKlasyfikacyjne get() = endpoints.PlusOkresyKlasyfikacyjne
    override val PlusOceny get() = endpoints.PlusOceny

    // wiadomosciplus
    override val Skrzynki get() = endpoints.Skrzynki
    override val Odebrane get() = endpoints.Odebrane
    override val OdebraneSkrzynka get() = endpoints.OdebraneSkrzynka
    override val Wyslane get() = endpoints.Wyslane
    override val WyslaneSkrzynka get() = endpoints.WyslaneSkrzynka
    override val Usuniete get() = endpoints.Usuniete
    override val UsunieteSkrzynka get() = endpoints.UsunieteSkrzynka
    override val WiadomoscOdpowiedzPrzekaz get() = endpoints.WiadomoscOdpowiedzPrzekaz
    override val WiadomoscNowa get() = endpoints.WiadomoscNowa
    override val MoveTrash get() = endpoints.MoveTrash
    override val Delete get() = endpoints.Delete
    override val RestoreTrash get() = endpoints.RestoreTrash
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

    val PlusContext: String
    val PlusAutoryzacjaPesel: String
    val PlusFrekwencja: String
    val PlusUsprawiedliwienia: String
    val PlusFrekwencjaStatystyki: String
    val PlusZarejestrowaneUrzadzenia: String
    val PlusRejestracjaUrzadzeniaToken: String
    val PlusZebrania: String
    val PlusRealizacjaZajec: String
    val PlusSprawdzianyZadaniaDomowe: String
    val PlusSprawdzianSzczegoly: String
    val PlusZadanieDomoweSzczegoly: String
    val PlusPlanZajec: String
    val PlusDniWolne: String
    val PlusUwagi: String
    val PlusNauczyciele: String
    val PlusInformacje: String
    val PlusDaneUcznia: String
    val PlusUczenZdjecie: String
    val PlusOkresyKlasyfikacyjne: String
    val PlusOceny: String

    // wiadomosciplus
    val Skrzynki: String
    val Odebrane: String
    val OdebraneSkrzynka: String
    val Wyslane: String
    val WyslaneSkrzynka: String
    val Usuniete: String
    val UsunieteSkrzynka: String
    val WiadomoscOdpowiedzPrzekaz: String
    val WiadomoscNowa: String
    val MoveTrash: String
    val Delete: String
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

    // uczenplus
    override val PlusContext = "Context"
    override val PlusAutoryzacjaPesel = "AutoryzacjaPesel"
    override val PlusFrekwencja = "Frekwencja"
    override val PlusUsprawiedliwienia = "Usprawiedliwienia"
    override val PlusFrekwencjaStatystyki = "FrekwencjaStatystyki"
    override val PlusZarejestrowaneUrzadzenia = "ZarejestrowaneUrzadzenia"
    override val PlusRejestracjaUrzadzeniaToken = "RejestracjaUrzadzeniaToken"
    override val PlusZebrania = "Zebrania"
    override val PlusRealizacjaZajec = "RealizacjaZajec"
    override val PlusSprawdzianyZadaniaDomowe = "SprawdzianyZadaniaDomowe"
    override val PlusSprawdzianSzczegoly = "SprawdzianSzczegoly"
    override val PlusZadanieDomoweSzczegoly = "ZadanieDomoweSzczegoly"
    override val PlusPlanZajec = "PlanZajec"
    override val PlusDniWolne = "DniWolne"
    override val PlusUwagi = "Uwagi"
    override val PlusNauczyciele = "Nauczyciele"
    override val PlusInformacje = "Informacje"
    override val PlusDaneUcznia = "DaneUcznia"
    override val PlusUczenZdjecie = "UczenZdjecie"
    override val PlusOkresyKlasyfikacyjne = "OkresyKlasyfikacyjne"
    override val PlusOceny = "Oceny"

    // wiadomosciplus
    override val Skrzynki = "Skrzynki"
    override val Odebrane = "Odebrane"
    override val OdebraneSkrzynka = "OdebraneSkrzynka"
    override val Wyslane = "Wyslane"
    override val WyslaneSkrzynka = "WyslaneSkrzynka"
    override val Usuniete = "Usuniete"
    override val UsunieteSkrzynka = "UsunieteSkrzynka"
    override val WiadomoscOdpowiedzPrzekaz = "WiadomoscOdpowiedzPrzekaz"
    override val WiadomoscNowa = "WiadomoscNowa"
    override val MoveTrash = "MoveTrash"
    override val Delete = "Delete"
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

    // uczenplus
    override val PlusContext = "7fbcc3fc-1021-444e-86ec-506683e02337"
    override val PlusAutoryzacjaPesel = "PlusAutoryzacjaPesel"
    override val PlusFrekwencja = "c1fc4853-7216-4fd1-9957-4426278bb0bc"
    override val PlusUsprawiedliwienia = "e3ae5a25-40ba-46f6-8e3f-fa325c68a300"
    override val PlusFrekwencjaStatystyki = "85ec4b45-092b-49ff-a280-89c8439d2e09"
    override val PlusZarejestrowaneUrzadzenia = "eca2a314-4aa1-4242-a032-82915863b00e"
    override val PlusRejestracjaUrzadzeniaToken = "PlusRejestracjaUrzadzeniaToken"
    override val PlusZebrania = "358477c6-2917-4a11-9208-8abdcde4d05c"
    override val PlusRealizacjaZajec = "25c2155a-ee89-4f0b-b1e2-000f7eebbf06"
    override val PlusSprawdzianyZadaniaDomowe = "65b13621-cafd-4e86-b7a2-093acc60f618"
    override val PlusSprawdzianSzczegoly = "81ee73f9-9b4f-415f-aa6a-2cb7edaae3cd"
    override val PlusZadanieDomoweSzczegoly = "fd6a4f90-5cb2-44f1-991b-fc4fbba8acbe"
    override val PlusPlanZajec = "b08b6215-ebac-481d-81b9-14807a098731"
    override val PlusDniWolne = "457deb32-212c-4564-8c2b-7941a1f091c9"
    override val PlusUwagi = "f0dcdc6d-cccf-42b9-86e3-02e7e9c5bb8d"
    override val PlusNauczyciele = "4e68fe23-18d2-42ef-94c4-27acbd4ab16c"
    override val PlusInformacje = "dcae87e5-17a7-40d5-b362-f6caa9162715"
    override val PlusDaneUcznia = "d7ff4abc-3a93-45d3-b28d-f4fb82fcd565"
    override val PlusUczenZdjecie = "86e7b08f-de0c-4cc7-8042-e3a3796f7090"
    override val PlusOkresyKlasyfikacyjne = "0669f1fd-e6f0-4007-ba4a-1d99c9107bb4"
    override val PlusOceny = "587b18fa-0cdd-4db9-9bc8-e2d67094b385"

    // wiadomosciplus
    override val Skrzynki = "787817a4-bfa8-472c-b1ce-bbe324b308cf"
    override val Odebrane = "fa8d0cb9-742e-4f9f-b3e2-65b7d5cd7aa6"
    override val OdebraneSkrzynka = "9545648d-6534-4282-acb7-6e9cc52ab402"
    override val Wyslane = "962f3256-b82a-42bd-8f20-f03d79723516"
    override val WyslaneSkrzynka = "a046f45d-42ae-4072-9684-e957a797a22b"
    override val Usuniete = "94c2e642-b8e3-41f7-8428-27c3f213cb5d"
    override val UsunieteSkrzynka = "bae7f54c-f18e-4553-92d6-9a5f93392b39"
    override val WiadomoscOdpowiedzPrzekaz = "44444524-ba04-4259-8853-88343cf294be"
    override val WiadomoscNowa = "4f02e91e-b9ad-4da1-b4d9-84f258ad1d12"
    override val MoveTrash = "b2131c71-8d76-4d93-9274-1d5b4f30c915"
    override val Delete = "cd379bae-9700-48b4-a0d0-9eca08ee908f"
    override val RestoreTrash = "e6de58f9-4db1-4cd2-afdf-ac9fcca37e43"
}

internal object ApiEndpoints_24_04_0003_58698 : IApiEndpoints {
    override val Autoryzacja = "b82c987d-8e94-4ed3-b46e-2d1818f2e0f8"
    override val DostepOffice = "9d9b5874-68f4-4f1e-bef5-372f1cb11d29"
    override val EgzaminySemestralne = "99b62670-2adc-4c17-9abc-6e3d42eec773"
    override val EgzaminyZewnetrzne = "4db8f093-352a-4a00-a299-0e9b819c8c58"
    override val EwidencjaObecnosci = "02429d97-5877-42ae-a44e-5dd7531e20b4"
    override val FormularzeSzablony = "e02fac63-909e-450c-a4ab-75bfd4865189"
    override val FormularzeSzablonyDownload = "FormularzeSzablonyDownload"
    override val FormularzeWysylanie = "b97e880e-ae8d-44b6-a48d-92c9be7d22f5"
    override val Frekwencja = "80dc9527-386d-4da5-b688-3c3a9c4c49a9"
    override val FrekwencjaStatystyki = "a6d0fa58-5613-45e4-8f37-e2e5cba26dd3"
    override val FrekwencjaStatystykiPrzedmioty = "baa947c6-915b-4498-afdb-11ec6b088d36"
    override val Homework = "302944cd-7f41-4de8-8c9f-02dd00990f62"
    override val Jadlospis = "738a6eff-3bb3-4ff7-9443-70e6e96b61ab"
    override val LekcjeZaplanowane = "59874a90-98f2-4aa2-a857-f029b88a667c"
    override val LekcjeZrealizowane = "23f18c73-b0eb-4a1a-b9d3-67b9f3513a12"
    override val Oceny = "1e1ecd09-aa17-46ac-8cf4-90b1bf0c837d"
    override val Ogloszenia = "ba99b004-dca2-41b4-af6b-13969546ce65"
    override val Oplaty = "d58b34e4-0220-4363-b08f-fa307230898d"
    override val PlanZajec = "e59d0f63-92e1-47d9-a8e1-293eed48e4b4"
    override val Platnosc = "2b5d4da7-8b0f-45e0-923d-0b20d327800a"
    override val PlatnoscMetadata = "ca5a0fa1-78f1-4fad-b7ac-59fbdf6bdaf4"
    override val PodrecznikiLataSzkolne = "0c3f9d64-9561-42ab-825e-e73b4097c2d0"
    override val PodrecznikiUcznia = "2e7a1def-cdc3-4944-862c-5e94207ae891"
    override val Pomoc = "0cde801a-9290-4780-9099-96f7a130308e"
    override val RejestracjaUrzadzeniaToken = "17786f90-2725-477f-98ac-f0e1bff11d7d"
    override val RejestracjaUrzadzeniaTokenCertyfikat = "de16ca24-4638-4b11-8b2d-6ed09ec82b8a"
    override val RozpoczeciePlatnosci = "90273adc-6308-47b1-be2b-b8d2297a1530"
    override val ScalanieKont = "95661b3e-b9ac-416c-bf35-f195994f8af5"
    override val Sprawdziany = "d6404626-e7e4-4093-8477-4eb964da19db"
    override val Statystyki = "f8308032-0674-4bf7-a7d5-2642d064ef24"
    override val SzkolaINauczyciele = "a5ec0ae3-f7eb-4548-a484-f725e0164634"
    override val Uczen = "f8aa0d01-bd77-44d5-aa23-2606afbc27f5"
    override val UczenCache = "21a5186d-2aab-4123-bad7-269aa7173bb2"
    override val UczenDziennik = "a01ea13f-14f0-4c56-8b91-790e5aeecdf1"
    override val UczenZdjecie = "b4a0f9aa-2a45-4e3d-bd9a-f1513e949f83"
    override val Usprawiedliwienia = "81db3fa0-fb76-401a-ae9e-0fdffc86d2ff"
    override val UwagiIOsiagniecia = "f63b7ca7-cfe2-4b0b-80cf-1ef1baed597d"
    override val ZarejestrowaneUrzadzenia = "5a98cd83-f542-4bd2-a1eb-c53d75360aa7"
    override val Zebrania = "fb20a581-5ad7-49ab-b640-ccdd3f74b0ea"
    override val ZebraniaObecnosc = "a057e980-c662-4573-b485-01072dab2c14"
    override val ZgloszoneNieobecnosci = "5c63a985-2149-4e7b-a30f-3692b16b69e3"

    // uczenplus
    override val PlusContext = "Context"
    override val PlusAutoryzacjaPesel = "AutoryzacjaPesel"
    override val PlusFrekwencja = "Frekwencja"
    override val PlusUsprawiedliwienia = "Usprawiedliwienia"
    override val PlusFrekwencjaStatystyki = "FrekwencjaStatystyki"
    override val PlusZarejestrowaneUrzadzenia = "ZarejestrowaneUrzadzenia"
    override val PlusRejestracjaUrzadzeniaToken = "RejestracjaUrzadzeniaToken"
    override val PlusZebrania = "Zebrania"
    override val PlusRealizacjaZajec = "RealizacjaZajec"
    override val PlusSprawdzianyZadaniaDomowe = "SprawdzianyZadaniaDomowe"
    override val PlusSprawdzianSzczegoly = "SprawdzianSzczegoly"
    override val PlusZadanieDomoweSzczegoly = "ZadanieDomoweSzczegoly"
    override val PlusPlanZajec = "PlanZajec"
    override val PlusDniWolne = "DniWolne"
    override val PlusUwagi = "Uwagi"
    override val PlusNauczyciele = "Nauczyciele"
    override val PlusInformacje = "Informacje"
    override val PlusDaneUcznia = "DaneUcznia"
    override val PlusUczenZdjecie = "UczenZdjecie"
    override val PlusOkresyKlasyfikacyjne = "OkresyKlasyfikacyjne"
    override val PlusOceny = "Oceny"

    // wiadomosciplus
    override val Skrzynki = "a04c1b46-48f5-4856-9eae-1efe9fface1a"
    override val Odebrane = "be2f38a5-f090-4d38-b78a-1e776d304b20"
    override val OdebraneSkrzynka = "4e096df8-70e9-4a7c-b72d-486d6d05d87d"
    override val Wyslane = "dee02521-890a-4701-9699-de9b1080f30a"
    override val WyslaneSkrzynka = "1aab4b35-fdeb-422d-9e62-b8337c2f1a31"
    override val Usuniete = "9a1d2b04-763e-4e5a-8835-e356f52ed9b4"
    override val UsunieteSkrzynka = "a3e8cd92-f3bd-4bf3-9a1b-63f20b475982"
    override val WiadomoscOdpowiedzPrzekaz = "WiadomoscOdpowiedzPrzekaz"
    override val WiadomoscNowa = "03051ddd-22dd-45a6-a214-211c8dab0f7d"
    override val MoveTrash = "724f1218-447a-4110-a9ce-8c9bc52547f5"
    override val Delete = "Delete"
    override val RestoreTrash = "caf5eacb-a2ab-44d7-82c1-bcbd0b7612ed"
}
