import {Component, OnInit} from '@angular/core';
import {EditProfileRequest} from '../../requests';
import {Router} from '@angular/router';
import {UserService} from '../../service/user.service';
import {AuthService} from '../../service/auth.service';
import {Location} from '@angular/common';
import {Toast} from "../../toast-notify";

@Component({
  selector: 'app-edit-profile',
  templateUrl: './edit-profile.component.html',
  styleUrls: ['./edit-profile.component.scss']
})
export class EditProfileComponent implements OnInit {

  request = new EditProfileRequest();

  constructor(private _location: Location,
              private router: Router,
              private auth: AuthService,
              private userService: UserService) {
  }

  ngOnInit() {
    const user = this.auth.user;
    this.request.username = user.username;

    this.userService.getProfile(user.username).subscribe(
      user => {
        this.request.bio = user.bio;
        this.request.firstName = user.firstName;
        this.request.lastName = user.lastName;
        this.request.visible = user.visible;
      },
      err => console.error(err),
      () => {
      }
    );
  }

  editProfile() {
    this.userService.editProfile(this.request).subscribe(
      () => {
      },
      err => {
        console.error(err);
        Toast.toast('Profile edit failed! ' + err.error.error.type);
      },
      () => {
        // update of the current user locally saved
        const user = {
          username: this.request.username,
          firstName: this.request.firstName,
          lastName: this.request.lastName,
          visible: this.request.visible,
          bio: this.request.bio,
          lastSeen: this.auth.user.lastSeen,
          token: this.auth.user.token,
          status: this.auth.user.status
        };
        this.auth.setUser(user);

        Toast.toast('The user profile has been successfully edited');
        this._location.back();
      }
    );
  }

}
