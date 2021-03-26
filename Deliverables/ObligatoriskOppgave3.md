# Obligatorisk oppgave 3: Prosjekt RoboRally

Project board at 26.03.21 11:45
![Project board 1](./images/project_board1_delivery3.png)
![Project board 2](./images/project_board2_delivery3.png)

## Deloppgave 1: Prosjekt og prosjektstruktur

### Roller:
Som sist så fungerer rollene på samme måte, og vi har ikke gjort noen endringer her. Teamlead og kundekontakt er
uforandret, og hele teamet stiller som developers, og fyller inn der det trengs. Sånn som det ser ut nå er det ikke
nødvendig å gjøre endringer her. 

#### Erfaringer og endringer:
Det er viktig å passe på at merge conflicts of alle checks passer når man åpner en pull request Ferdige arbeidsoppgaver
og tempo har vært veldig ubalansert, noen på teamet blir fort ferdig mens andre har brukt lengre tid. Dette fører til at
det blir mye dødtid der teamet venter på at noe skal bli kodet/gjort ferdig av noen andre. Endringer vi har gjort her:
ny prosjektmetodikk, vi har laget fremdriftsplan for teamet, med ulike tasks som skal bli gjort hver uke, og en task har
blitt assignet til en developer på teamet. Dette putter press på å få gjort ferdig oppgaver i tide, og legge inn mer
effort for å få det gjort. Dette synes teamet er en god løsning, og kan føre til bedre resultat og mer effektiv jobbing.
Når kommunikasjonen er god lever vi bedre produkt Lurt å lage ny branch for hver task 

#### Gruppedynamikken:
God sånn som sist. Dersom noen sitter fast hjelper resten av teamet, dersom noen trenger en “push” får de dette. Ros og
konstruktiv kritikk er på plass for å sørge for at god kode og gode resultat blir gitt. Vi får ikke møtt alle fysisk på
grunn av restriksjoner og at folk ikke er i byen, så grunnlaget for dynamikken blir for det meste lagt digitalt.

### Kommunikasjon:
Går framover, vi har blitt enige at dersom man sitter fast på noe, enten på grunn av tekniske problem eller annet, skal
vi kontakte teamet umiddelbart, så alle vet hva som foregår, hvor vi ligger ann og hva som må bli prioritert. Dette har
ført til bedre kommunikasjon over Discord. Vi tar og i bruk møtene vi har satt opp på mandagene til diskutering og
kodehjelp. 

#### Forbederedningspunkter fra retroperspektivet:
Teamet ble enige om å ha mer balansert commits. Dette har vi klart bra, og nå har fordelingen blitt mye bedre enn den
var sammenlignet med oblig 1. Vi ville prøve å få inn en fast PR hver i uken, dette ble det litt problemer med, og vi
har endret litt på dette. Som nevnt ovenfor har vi nå en fremdriftsplan vi skal følge, for å få en oversikt over hva
alle gjør og skal gjøre. Vi på teamet er fornøyd med denne så langt. Mesteparten av planlegging skjer nå på
fredagsmøtene, da kan man prøve seg litt fram i helgen og få litt erfaringer, hva er vanskelig, hva som må endres. På
mandagsmøtet kan vi diskutere disse og kjøre litt parprogammering om nødvendig. Kontinuerlig kommunikasjon på discord
har blitt bedre. 

#### Forbedringspunkt vi ønsker å følge opp under neste sprint:
Mer effektiv jobbing, og flere tasks gjort (gjøre opp litt erfaringer med fremdriftsplanen og om denne fungerer bra
Dersom en på teamet sitter fast med en task, bør man bli flinkere til å rapportere dette inn med en gang, sånn at teamet
vet hvordan vi ligger ann. 

#### Oppgaver fremover:
Som nevnt ovenfor har vi nå laget fremdriftsplan. Her er det ulike tasks som må bli gjort hver uke, dersom noen sitter
fast bistår resten av teamet og vi sammarbeider, om oppgaven er for vanskelig for noen på teamet kan vi bytte.
Fremdriftsplanen er en foreløpig draft for å ha litt kontroll over hva som må bli gjort, og for å ha litt press på
teamet.
![tasks_next_month.png](images/delivery3/tasks_next_month.png)

## Deloppgave 3: Kode

Forenklet klassediagram:
![Class diagram1](./images/delivery3/class_diagram1.png)
![Class diagram2](./images/delivery3/class_diagram2.png)

### Manuelle tester:

1. Gitt at menyen vises
    1. Bør jeg kunne flytte meg
        1. Til nye skjermer
        2. Tilbake til den skjermen jeg kom ifra
    2. Bør der være klart for spilleren hvordan å starte et spill med spillmodusen de ønsker
2. Gitt at spillet er igang (ikke implementert)
    1. Bør det alltid være en måte å komme tilbake til hovedmenyen
        1. Dersom spillet er slutt, skal brukeren sendes tilbake til menyen

2. Gitt at jeg er en spiller og nettopp har startet spillet skal jeg:
    1. Lett kunne få opp et spillbrett på skjermen med en bestemt størrelse (bare et 5x5 brett er tilgjengelig for
       øyeblikket)
    2. Kunne se og skjønne hvordan spillbrettet er lagt opp, og at det er logisk satt sammen (ut i fra reglene på
       spillet)

3. Gitt at jeg er en spiller på et aktivt spill skal jeg:
    1. Kunne se forskjell på alle tiles og objekt på spillet
    2. Bli informert om hva de ulike elementene er og gjør
    3. Få opp et brett med klare skiller mellom grafikken, og se et brett bestående av element med grafikk som henger
       sammen med funksjonaliteten til elementet.
    4. Så på brettet når spiller brikkene flytter seg.
    5. Se forandring på objekt på brettet dersom et flag blir besøkt
    6. Bli oppmerksom på dette gjennom nye element på brettet, og element som spesifikk symboliserer seier
    7. Få opp en melding på skjermen, eller i terminal som forklarer meg hva som skjer