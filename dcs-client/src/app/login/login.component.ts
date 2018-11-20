import {Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {AuthService} from '../service/auth.service';
import {LoginRequest} from '../requests';
import {Toast} from "../toast-notify";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {

  request = new LoginRequest();

  constructor(private router: Router, private auth: AuthService) {}

  ngOnInit() {}

  login() {
    this.auth.login(this.request).subscribe(
      user => console.log(user),
      err => {
        const error = err.error.error;
        this.request.password = '';
        Toast.toast("Login failed! \n" + error.type +
          " error. \n Make sure to enter the correct credentials");
        console.error(err);
      },
      () => this.router.navigateByUrl('/')
    );
  }

}
