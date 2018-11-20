import {Component, OnInit} from '@angular/core';
import {User} from "../../model";
import {UserService} from "../../service/user.service";
import {ActivatedRoute} from "@angular/router";

@Component({
  selector: 'app-user-profile',
  templateUrl: './user-profile.component.html',
  styleUrls: ['./user-profile.component.scss']
})
export class UserProfileComponent implements OnInit {

  user: User;

  constructor(private userService: UserService, private route: ActivatedRoute) { }

  ngOnInit() {
    this.route.params.subscribe(params => {
      const username = params['username'];
      this.userService.getProfile(username)
        .subscribe(userRetrieved => {
          this.user = userRetrieved;
          console.log(userRetrieved);
        });
    });
  }

}
