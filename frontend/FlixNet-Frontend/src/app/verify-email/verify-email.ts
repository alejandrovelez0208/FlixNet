import { Component, OnInit, signal } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { AuthService } from '../shared/services/auth-service';

@Component({
  selector: 'app-verify-email',
  standalone: false,
  templateUrl: './verify-email.html',
  styleUrl: './verify-email.css',
})
export class VerifyEmail implements OnInit {
  loading = signal(true);
  success = signal(false);
  message = signal('');

  constructor(private route: ActivatedRoute, private authService: AuthService) { }

  ngOnInit(): void {
    const token = this.route.snapshot.queryParamMap.get('token');

    if (!token) {
      this.loading.set(false);
      this.success.set(false);
      this.message.set('Invalid verification link. No token provided.')
      return;
    }

    this.authService.verifyEmail(token).subscribe({
      next: (response: any) => {
        this.loading.set(false);
        this.success.set(true);
        this.message.set(response.message || 'Email verified successfully! You can now login.')
      },
      error: (err) => {
        this.loading.set(false);
        this.success.set(false);
        this.message = err.error?.error || 'Verification failed. The link may have expired or is invalid.'
      }
    });
  }
}
