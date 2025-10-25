package com.hashsoft.audiotape.data


sealed interface AudioSearchObject {
    data class Direct(val searchPath: String) : AudioSearchObject
    data class Relative(val volumeName: String, val relativePath: String, val name: String) :
        AudioSearchObject

}


