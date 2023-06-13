package com.omh.android.auth.gms

import android.content.Context
import androidx.annotation.Keep
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.omh.android.auth.api.OmhAuthClient
import com.omh.android.auth.api.OmhAuthFactory

@Keep
internal object OmhAuthFactoryImpl : OmhAuthFactory {
    @Suppress("UNUSED_PARAMETER")
    override fun getAuthClient(
        context: Context,
        scopes: Collection<String>,
        clientId: String
    ): OmhAuthClient {
        val scopeList: MutableList<Scope> = scopes.map(::Scope).toMutableList()
        val gsoBuilder = GoogleSignInOptions.Builder()
        scopeList.forEach(gsoBuilder::requestScopes)
        val client: GoogleSignInClient = GoogleSignIn.getClient(context, gsoBuilder.build())
        return OmhAuthClientImpl(client)
    }

}
