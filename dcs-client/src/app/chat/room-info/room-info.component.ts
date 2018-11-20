import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {Participation} from "../../model";
import {ChatService} from "../../service/chat.service";
import {UserService} from "../../service/user.service";
import {AuthService} from "../../service/auth.service";
import {remove} from 'lodash'
import {Toast} from "../../toast-notify";

@Component({
  selector: 'app-room-info',
  templateUrl: './room-info.component.html',
  styleUrls: ['./room-info.component.scss']
})
export class RoomInfoComponent implements OnInit {

  name: string;

  participations: Participation[] = [];

  constructor(private route: ActivatedRoute, private router: Router,
              private service: ChatService, private userService: UserService,
              private authService: AuthService) {
  }

  ngOnInit() {
    this.route.params.subscribe(params => {
      this.name = params['name'];
      this.getRoomParticipations()
    });


    this.service.onRoomJoined()
      .subscribe(participation => {
        if (participation.room.name === this.name) this.participations.push(participation)
      });

    this.service.onRoomLeft()
      .subscribe(participation => {
        console.log(participation);
        if (participation.name === this.name) remove(this.participations,
            part => part.username === participation.username)
      });
  }

  getRoomParticipations() {
    this.service.getRoomParticipations(this.name)
      .subscribe(participations => this.participations = participations)
  }

  openUserProfile(username: string) {
    this.userService.getProfile(username).subscribe(user => {
      if (!this.authService.user.visible && !this.isCurrentUser(user)){
        Toast.toast("You are invisible so you can't visualize the profile of other users");
      } else if (user.visible || this.isCurrentUser(user)) {
        this.router.navigate(['/users', username, 'profile']);
      } else {
        Toast.toast("This user profile is invisible");
      }
    })
  }

  private isCurrentUser(user) {
    return user.username === this.authService.user.username;
  }
}
