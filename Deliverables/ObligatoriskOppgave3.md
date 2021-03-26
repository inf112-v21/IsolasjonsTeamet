# Obligatorisk oppgave 3: Prosjekt RoboRally

Project board at 26.03.21 11:45
![Project board 1](./images/project_board1_delivery3.png)
![Project board 2](./images/project_board2_delivery3.png)

## Deloppgave 3: Kode

Forenklet klassediagram:
![Class diagram](./images/classes_delivery3.png)

Manuelle tester:

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