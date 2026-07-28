import { Component, signal } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AuthService } from '../shared/services/auth-service';
import { Router } from '@angular/router';
import { NotificationService } from '../shared/services/notification-service';

@Component({
  selector: 'app-forgot-password',
  standalone: false,
  templateUrl: './forgot-password.html',
  styleUrl: './forgot-password.css',
})
export class ForgotPassword {
  forgotPassword !: FormGroup;
  loading = signal(false);

  constructor(private fb: FormBuilder,
    private authService: AuthService,
    private notification: NotificationService,
    private router: Router) {
    this.forgotPassword = this.fb.group({
      email: ['', [Validators.required, Validators.email]]
    });
  }

  submit() {
    this.loading.set(true);
    const email = this.forgotPassword.value.email?.trim().toLowerCase();

    this.authService.forgotPassword(email).subscribe({
      next: (response: any) => {
        this.loading.set(false);
        this.notification.success(response.message);
        this.router.navigate(['/login']);
      },
      error: (err) => {
        this.loading.set(false);
        this.notification.error(err.error?.error || 'Failed to send reset email. Please try again.');
      }
    });
  }
}
