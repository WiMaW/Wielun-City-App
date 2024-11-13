package com.wioletamwrobel.wieluncityapp.model

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.wioletamwrobel.wieluncityapp.R

object Dialog {

    @Composable
    fun CreateDialog(
        icon: @Composable () -> Unit,
        title: String,
        dialogText: String,
        onConfirmButtonClicked: () -> Unit,
        onConfirmButtonText: String,
        onDismissButtonClicked: () -> Unit,
    ) {
        AlertDialog(
            icon = icon,
            title = {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                )
            },
            text = {
                Text(
                    text = dialogText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
                },
            onDismissRequest = onDismissButtonClicked,
            confirmButton = {
                TextButton(
                    onClick = onConfirmButtonClicked
                ) {
                    Text(
                        onConfirmButtonText,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = onDismissButtonClicked
                ) {
                    Text(
                        stringResource(R.string.dialog_dismissButton_text),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        )
    }
}