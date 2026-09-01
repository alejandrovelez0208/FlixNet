import { Injectable } from '@angular/core';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { ChangePasswordDialog } from '../components/change-password-dialog/change-password-dialog';
import { D } from '@angular/cdk/keycodes';
import { DIALOG_CONFIG } from '../constants/app.contants';
import { Observable } from 'rxjs';
import { ConfirmDialog } from '../components/confirm-dialog/confirm-dialog';

@Injectable({
  providedIn: 'root',
})
export class DialogService {

  constructor(private dialog: MatDialog) { }

  openChangePasswordDialog(): MatDialogRef<ChangePasswordDialog> {
    return this.dialog.open(ChangePasswordDialog, DIALOG_CONFIG.CHANGE_PASSWORD)
  }

  openConfirmation(title: string, message: string, confirmText: String = 'Confirm', cancelText: String = 'Cancel', type: 'warning' | 'danger' | 'info' = 'warning'): Observable<boolean> {
    const dialogRef = this.dialog.open(ConfirmDialog, {
      ...DIALOG_CONFIG.CONFIRM,
      data: {
        title,
        message,
        confirmText,
        cancelText,
        type
      }
    });
    return dialogRef.afterClosed();
  }
}
