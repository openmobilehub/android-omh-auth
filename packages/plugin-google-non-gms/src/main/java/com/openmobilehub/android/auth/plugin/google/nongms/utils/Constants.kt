/*
 * Copyright 2023 Open Mobile Hub
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.openmobilehub.android.auth.plugin.google.nongms.utils

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
}
