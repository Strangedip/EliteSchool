import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { Role } from '../auth/enums/roles.enum';

@Injectable({
  providedIn: 'root'
})
export class RoleService {

  private rolesSubject = new BehaviorSubject<Role[]>([]);
  roles$: Observable<Role[]> = this.rolesSubject.asObservable();

  constructor() {}

  // After login, call this
  setRoles(roles: Role[]) {
    this.rolesSubject.next(roles);
  }

  hasRole(role: Role): boolean {
    return this.rolesSubject.value.includes(role);
  }

  hasAnyRole(allowedRoles: Role[]): boolean {
    return allowedRoles.some(role => this.rolesSubject.value.includes(role));
  }
}
