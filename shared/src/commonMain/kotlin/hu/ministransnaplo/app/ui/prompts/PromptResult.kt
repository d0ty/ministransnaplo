/*
 * Copyright 2026 doty and László Rab
 * Use of this source code is governed by the GNU General Public License that can be found at the LICENSE file
 */

package hu.ministransnaplo.app.ui.prompts

import kotlinx.serialization.Serializable

/**
 * Prompt result wrapper.
 * Subtypes:
 *  - [PromptResult.Email] results of the [EmailPrompt] dialog.
 *
 *  Every prompt should have a subtype.
 */
@Serializable
sealed interface PromptResult {
    /**
     * Wrapper of [EmailPrompt] results.
     *
     * @param request The request that was made to the user, for receiver side identification.
     * @param email The email address that the user provided.
     */
    @Serializable
    data class Email(val request: EmailPromptRequests, val email: String) : PromptResult

    @Serializable
    data class DestructiveAction(val request: DestructivePromptRequests, val confirmed: Boolean) : PromptResult
}
