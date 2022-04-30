package cz.abo.b2b.web.security.users

enum class Tarif(var message: String) {
    TRIAL_3_MONTHS("Chci si aplikaci vyzkoušet na 3 měsíce - žádná měsíční platba"),
    IN_LOSS("Můj krámek ten generuje finanční ztrátu - žádná měsíční platba"),
    WITHOUT_PROFIT("Můj krámek nedosahuje zisku, ale ani ztráty - 100Kč měsíčně"),
    PROFITABLE("Můj krámek je ziskový - 300Kč měsíčně")
}