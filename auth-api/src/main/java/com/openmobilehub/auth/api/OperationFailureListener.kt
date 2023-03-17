package com.openmobilehub.auth.api

/**
 * Helper SAM interface for Java interoperability. Lambdas don't translate well to Java.
 */
fun interface OperationFailureListener  {
    fun onFailure(exception: Exception)
}
