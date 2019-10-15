package uk.themeadow.doineedacache.uk.themeadow.doineedacache.model

sealed class MviValues {
}

class Loading: MviValues()
class Complete: MviValues()
data class error(val errorMessage: String): MviValues()
class ShowChooser: MviValues()