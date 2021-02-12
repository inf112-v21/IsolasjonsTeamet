# Obligatorisk oppgave 1: Prosjekt RoboRally - oppstart

## Deloppgave 1:

Organisering av teamet

Gruppenavn: **IsolasjonsTeamet**

### Roller:

**Teamlead:** Kathryn (mest erfaring med prosjektarbeid og koding)
**Kundekontakt:** Kasper (god kodekompetanse, god til å kommunisere og dokumentere underveis)
**Developers:** Noora, Kim, Espen (god kodekompetanse, foreløpig satt til developers, dersom andre roller trengs vil vi
endre dette underveis)

Alle på teamet går på IT studier og tar eller har tatt opp fag som er relevante for å løse denne oppgaven. Erfaring med
Java og bruk av git kommer godt med, og flere av oss har vært innom dette. Teamet kommer til å ta i bruk nye verktøy
under oppgaven, og kompetansen for alle vil utvide seg. Teamlead har mest erfaring fra lignende prosjekt, og høyere
kodekompetanse, og er da et naturlig valg som teamlead.

### Oppgaver

For denne første obligen har vi fokusert på å sørge for at alle på teamet forstår hvordan utviklingsprosessen, og skal
være komfortabel med den. I dette legger vi at alle skal ha laget en PR med noe kode, gitt code reviews på andre PRer,
og fått PRene sine merget.

### Project board

Vi har satt opp projectboard på git. Denne skal og har vi brukt flittig for å føre opp planlagte oppgaver, priortere de
alt etter behov og få oversikt over det vi allerede har løst. Alle på teamet skal få oversikt over denne og vite hvordan
den funker.

## Deloppgave 2:

Velg og tilpass en prosess for laget

### Prosjektmetodikk

Vi kommer til å kjøre kjent Kanban stil og jobbe med et project board for å organisere arbeidet vårt. Teamet ønsker ikke
å gjøre det så komplisert i starten, og det kan hende vi må gjør endringer underveis, og når nye oppgaver skal løses.

### Kommunikasjon

Discord er en god platform for både fildeling, kommunikasjon og skjermdeling. Vi har derfor valgt dette som platform for
kommunikasjon. Her har vi en tekst channel der teamet hyppig skal oppdatere hverandre på hva som skjer, og holde teamet
oppdatert på når og hva som må løses.

### Code reviews / oppfølging av arbeid

Teamet ønsker å oppfølge koden og løsningene til hverandre på en god måte, da det er viktig å få andre sine synspunkt og
perspektiv. Repositoriet vårt på github er satt opp sånn at alle kan si sin mening og nye endringer, og approve de
dersom de oppnår standard. Etter 2 antall approves kan disse endringene bli merget inn i hovedprosjekt.

### Kode stil

For å oppeholde kodestilen i prosjektet har vi valgt å ikke bruke Codacy, men istedenfor bruke CheckStyle sammen med en
dedikert Github Action som sjekker at CheckStyle passerer. Dette gjør vi slik at vi kan se indikasjon på om kodestilen
følges også i IDEen, og slik at vi let kan forandre på kodestilen om vi trenger. Kode stilen vår er en modifisert
version av Google's Java kode stil, med tabs istedenfor mellomrom, tabs størelse på 4 maks linjevide på 120, og noen par
andre forandringer.

### CI

Vi har valgt å bruke Github Actions ettersom at det er både raskere, lar oss teste mer, og enklere of konfigurere enn
Travis CI.

### Møtetider

Teamet skal fast bruke gruppetimene på fredager til å diskutere ulike ting. Vi har bestemt at dette skal være en "final
review sessions" da innlevering av oppgaver og framdrift ofte faller på fredager, og vi da har den siste gruppetimen på
dagen obliger/oppgaver skal inn til en siste finpuss. Akkurat nå har vi ikke satt noen faste andre tidspunkt, men vi
fordeler oppgaver på den ene gruppetimen, og holder kontakt og jobber gjennom oppgavene sammen via Discord fortløpende.

### Dokumenter og deling

Github blir vår hovedplass for deling av data og filer. Vi bruker også google drive og google docs for deling av
informasjon og dokumentasjon. Discord er også mulig for oss å dele filer of informasjon over dersom ønskelig, og github
og discord kommuniserer og poster updates fra github repositoriet vårt, direkte i en text-channel i Discord for "easy
access".

## Deloppgave 3:

Spesifikasjon.

I Denne obligen har vi sett på spillet Roborally og hvordan det fungerer.

Vi skal lage et ferdig produkt av spillet med en kode som er ryddig og fungerer bra. Spillet går utpå at det er flere
robboter som spiller mot hverandre i programmerte steg til en av spillerene har nådd alle checkpoints som gjør at denne
spilleren vinner. Det er flere instruksjoner som for eksempel hvordan spillerene kan gå, forskjellige kort som gir
forskjellige instrukser og hva som skjer dersom en robot møter på en hindring eller en annen robot som også blir sett på
som en hindring.

En stor del av oppgaven handler om å jobbe sammen for at disse spesifikasjonene blir møtt og at vi får et ferdig produkt
med god leselig kode. Slik vi har satt det opp. I Card mappen vår finner vi klasser for spesifikasjoner angående
kortene, som hvilke kort som blir prioritert og hvordan hvert enkelt kort påvirker spillebrikken. I Actions mappen
finner vi ulike metoder for hvordan en robot kan oppføre seg og vi har en klasse for hver hendelse. En robot kan bevege
seg fremover, til høgre, til venstre, ta en "u-turn" og gå bakover. Tiles mappen er laget for å spesifisere hvilke type
tiles vi møter på i spillet. Disse kan også påvirke spillet videre.

Vi har valgt å bruke forskjellige brancher for å best mulig kode sammen. Vi har foreløpig ikke lagt så mye fokus på
merging, men har nå lært oss å bruke pull requests til å verifisere endringer hver enkeltperson har gjort slik at vi
alle kan se over og bestemme sammen at vi får et godt ferdig produkt. Merging vil videre ha større vektlegging siden vi
vil verifisere koden før vi legger det inn i master branchen i spillet vårt. Derfor bruker vi forskjellige brancher slik
at vi lett kan få oversikt over de forskjellige delene av koden.

## Deloppgave 4:

Kode

Ved den første obligen skal vi levere MVP for produktet, og har ulike punkter vi skal innom og implementere som kode. Vi
har implementert visualisering av brettet, og ulike element på brettet, bevegelse på brettet i ulike retninger,
endringer av grafikk dersom win/loss condition bli triggered, og out-of-bounds handling. Dette har vi gjort på github,
sjekk "Isolasjonsteamet" repository der for besvarelsen vår.

### Brukerhistorier:

1. Som bruker ønsker jeg å kunne se spillbrettet for å planlegge trekk fremover
    * Det er nødvendig at bruker skal kunne få fram brettet, skjønne det og vite hva som skjer. Bruker må ha mulighet
      til å planlegge fremover og lett se hva som er hva på brettet.
    * Gitt at jeg er en spiller og nettopp har startet spillet skal jeg:
        * Lett kunne få opp et spillbrett på skjermen med en bestemt størrelse
        * Kunne se og skjønne hvordan spillbrettet er lagt opp, og at det er logisk satt sammen (ut i fra reglene på
          spillet)

2. Som bruker ønsker jeg at det skal være mulig å se forskjell på de ulike elementene på brettet for å ikke skape
   forvirring og gjøre hensikten klar
    * Det er nødvendig at de ulike elementene på brettet har forksjelige utseender og ulik grafikk, så det er klart
      skille mellom de og hva deres purpose er.
    * Gitt at jeg er en spiller på et aktivt spill skal jeg:
        * Kunne se forskjell på alle tiles og objekt på spillet
        * Bli informert om hva de ulike elementene er og gjør
        * Få opp et brett med klare skiller mellom grafikken, og se et brett bestående av element med grafikk som henger
          sammen med funksjonaliteten til elementet.

3. Som bruker ønsker jeg å kunne flytte roboten min rundt på brettet for å ha mobilitet
    * Det er nødvendig at spiller skal klare å flytte roboten rundt. Spillet går ut på å gi trekk som endrer robotens
      posisjon, og prøve å besøke flagg for å få poeng. Det er da en kritisk funksjon ved spillet vi må ha med.
    * Gitt at jeg er en spiller på et aktivt spill skal jeg:
        * Kunne flytte roboten rundt på brettet
        * Lett bli informert om hvordan jeg kan gjøre dette, og hva kontroller som må bli brukt, gjerne i terminalen
          eller i en egen tekstbox
        * Kunne flytte roboten i de retningene jeg ønsker
        * Kunne flytte roboten på plasser reglene tillater, og ikke bli blokkert på plasser der jeg ikke skal bli
          blokkert på (ut i fra spillets logikk)

4. Som bruker ønsker jeg å kunne se om et flag blir besøkt, så jeg kan se om noen vinner
    * Det er nødvendig å ha med win/loss conditions i spillet, og gi bruker beskjed om dette for at bruker til en hver
      tid skal vite status til roboter på brettet.
    * Gitt at jeg er en spiller og har et aktivt spill vil jeg:
        * Se forandring på objekt på brettet dersom et flag blir besøkt
        * Bli oppmerksom på dette gjennom nye element på brettet, og element som spesifikk symboliserer seier
        * Få opp en melding på skjermen, eller i terminal som forklarer meg hva som skjer

## Oppsumering

Alle fikk til slutt gjort sine bidrag og vi fikk kontroll på hvordan vi laget pullrequests og code review etterhvert. Vi
forventet at vi skulle jobbe raskere enn vi faktisk gjorde men etter et par dager gikk det litt raskere i tempo i og med
at vi fikk satt oss bedre inn i oppgaven og hvordan vi gjorde ting etterhvert. Kommunikasjonen var litt stille i
begynnelsen på grunn av dette men vi fikk snakket bedre sammen etter en liten stund.

Kathryn gjorde veldig mye for neste steg av oppgaven og Kasper fikk fikset store deler av merging med main branchen vår.
Espen fikk fikset sine oppgaver i Tiles og ordnet i klassene der mens Kim fikk fikset prioritering av kort og satt seg
inn i hvordan de forskjellige kortene fungerer noe vi kan bruke videre i kodingen da vi har oversikt over hvordan vi kan
implementere disse i spillet. Noora har fikset action klasser slik at vi har oversikt over alle ting som kan skje med en
spiller eller en robot.

Videre har vi lyst til å fokusere mer på kommunikasjon og tempo i arbeidet i tillegg til at vi kan sette oss mer inn i
merging. Vi skal også begynne med mer koding og testing i de forskjellige klassene. 
