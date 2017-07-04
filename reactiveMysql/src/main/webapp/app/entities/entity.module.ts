import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';

import { ReactiveMysqlBankAccountModule } from './bank-account/bank-account.module';
import { ReactiveMysqlLabelModule } from './label/label.module';
import { ReactiveMysqlOperationModule } from './operation/operation.module';
/* jhipster-needle-add-entity-module-import - JHipster will add entity modules imports here */

@NgModule({
    imports: [
        ReactiveMysqlBankAccountModule,
        ReactiveMysqlLabelModule,
        ReactiveMysqlOperationModule,
        /* jhipster-needle-add-entity-module - JHipster will add entity modules here */
    ],
    declarations: [],
    entryComponents: [],
    providers: [],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class ReactiveMysqlEntityModule {}
