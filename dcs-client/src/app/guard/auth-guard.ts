import {ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot} from '@angular/router';
import {Injectable} from '@angular/core';
import {AuthService} from '../service/auth.service';

@Injectable()
export class AuthGuard implements CanActivate {

  constructor(private router: Router, private auth: AuthService) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): boolean {
    const authenticated = this.auth.isAuthenticated();
    const url = state.url;

    switch (url) {
      case '/login':
      case '/register':
        if (authenticated) {
          this.router.navigateByUrl('/');
          return false;
        }
        break;
      default:
        if (!authenticated) {
          this.router.navigateByUrl('/login');
          return false;
        }
        break;
    }
    return true;
  }

}
