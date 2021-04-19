package operations

object MapOperations {

    fun byteArrayToMap(byteArray: ByteArray) : Map<String, String> {
        return String(byteArray)
            .filter { it !in listOf('{', '}') }
            .split(", ")
            .map { it.split('=') }
            .associate{ it[0] to it[1] }
    }

    fun <K, V>mapToByteArray(map: Map<K, V>) : ByteArray {
        return map.toString().toByteArray()
    }

}