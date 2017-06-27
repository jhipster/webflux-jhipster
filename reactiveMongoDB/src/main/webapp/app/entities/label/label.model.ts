import { BaseEntity } from './../../shared';

export class Label implements BaseEntity {
    constructor(
        public id?: string,
        public label?: string,
    ) {
    }
}
