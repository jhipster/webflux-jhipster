import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { DefaultMongoDbSharedModule } from '../../shared';
import { DefaultMongoDbAdminModule } from '../../admin/admin.module';
import {
    BankAccountService,
    BankAccountPopupService,
    BankAccountComponent,
    BankAccountDetailComponent,
    BankAccountDialogComponent,
    BankAccountPopupComponent,
    BankAccountDeletePopupComponent,
    BankAccountDeleteDialogComponent,
    bankAccountRoute,
    bankAccountPopupRoute,
} from './';

const ENTITY_STATES = [
    ...bankAccountRoute,
    ...bankAccountPopupRoute,
];

@NgModule({
    imports: [
        DefaultMongoDbSharedModule,
        DefaultMongoDbAdminModule,
        RouterModule.forRoot(ENTITY_STATES, { useHash: true })
    ],
    declarations: [
        BankAccountComponent,
        BankAccountDetailComponent,
        BankAccountDialogComponent,
        BankAccountDeleteDialogComponent,
        BankAccountPopupComponent,
        BankAccountDeletePopupComponent,
    ],
    entryComponents: [
        BankAccountComponent,
        BankAccountDialogComponent,
        BankAccountPopupComponent,
        BankAccountDeleteDialogComponent,
        BankAccountDeletePopupComponent,
    ],
    providers: [
        BankAccountService,
        BankAccountPopupService,
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class DefaultMongoDbBankAccountModule {}
