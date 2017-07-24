import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';

import { DefaultMongoDbBankAccountModule } from './bank-account/bank-account.module';
import { DefaultMongoDbLabelModule } from './label/label.module';
import { DefaultMongoDbOperationModule } from './operation/operation.module';
/* jhipster-needle-add-entity-module-import - JHipster will add entity modules imports here */

@NgModule({
    imports: [
        DefaultMongoDbBankAccountModule,
        DefaultMongoDbLabelModule,
        DefaultMongoDbOperationModule,
        /* jhipster-needle-add-entity-module - JHipster will add entity modules here */
    ],
    declarations: [],
    entryComponents: [],
    providers: [],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class DefaultMongoDbEntityModule {}
