import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {Participation} from "../../model";
import {ChatService} from "../../service/chat.service";
import {UserService} from "../../service/user.service";
import {AuthService} from "../../service/auth.service";

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
      })
  }

  getRoomParticipations() {
    this.service.getRoomParticipations(this.name)
      .subscribe(participations => this.participations = participations)
  }

  openUserProfile(username: string) {
    this.userService.getProfile(username).subscribe(user => {
      if (user.visible || user.username === this.authService.user.username) {
        this.router.navigate(['/users', username, 'profile']);
      } else {
        alert("This user profile can't be shown");
      }
    })

  }
}
