import { BaseEntity, User } from './../../shared';

export class BankAccount implements BaseEntity {
    constructor(
        public id?: string,
        public name?: string,
        public balance?: number,
    ) {
    }
}
