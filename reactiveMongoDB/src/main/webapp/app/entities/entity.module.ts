import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';

import { ReactiveMongoDbBankAccountModule } from './bank-account/bank-account.module';
import { ReactiveMongoDbLabelModule } from './label/label.module';
import { ReactiveMongoDbOperationModule } from './operation/operation.module';
/* jhipster-needle-add-entity-module-import - JHipster will add entity modules imports here */

@NgModule({
    imports: [
        ReactiveMongoDbBankAccountModule,
        ReactiveMongoDbLabelModule,
        ReactiveMongoDbOperationModule,
        /* jhipster-needle-add-entity-module - JHipster will add entity modules here */
    ],
    declarations: [],
    entryComponents: [],
    providers: [],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class ReactiveMongoDbEntityModule {}
