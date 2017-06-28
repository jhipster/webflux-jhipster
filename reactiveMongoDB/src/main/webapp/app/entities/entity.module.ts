import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';

import { ReporeactiveMongoDbBankAccountModule } from './bank-account/bank-account.module';
import { ReporeactiveMongoDbLabelModule } from './label/label.module';
import { ReporeactiveMongoDbOperationModule } from './operation/operation.module';
/* jhipster-needle-add-entity-module-import - JHipster will add entity modules imports here */

@NgModule({
    imports: [
        ReporeactiveMongoDbBankAccountModule,
        ReporeactiveMongoDbLabelModule,
        ReporeactiveMongoDbOperationModule,
        /* jhipster-needle-add-entity-module - JHipster will add entity modules here */
    ],
    declarations: [],
    entryComponents: [],
    providers: [],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class ReporeactiveMongoDbEntityModule {}
