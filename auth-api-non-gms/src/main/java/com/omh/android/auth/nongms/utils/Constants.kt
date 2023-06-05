package com.omh.android.auth.nongms.utils

internal object Constants {
    const val PARAM_REDIRECT_URI = "redirect_uri"
    const val PARAM_CLIENT_ID = "client_id"
    const val PARAM_RESPONSE_TYPE = "response_type"
    const val PARAM_SCOPE = "scope"
    const val PARAM_CODE_CHALLENGE = "code_challenge"
    const val PARAM_CHALLENGE_METHOD = "code_challenge_method"
    const val SHA256 = "S256"

    const val SHARED_PREFS_TOKEN_FORMAT = "token_storage_%s"
    const val PROVIDER_GOOGLE = "google"
    const val EMAIL_KEY = "email"
    const val NAME_KEY = "given_name"
    const val SURNAME_KEY = "family_name"
    const val PICTURE_KEY = "picture"
    const val ID_KEY = "id"
    const val CAUSE_KEY = "cause"
    const val ID_TOKEN = "idtoken"
}
