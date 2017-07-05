import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';

import { DefaultCassandraBankAccountModule } from './bank-account/bank-account.module';
import { DefaultCassandraLabelModule } from './label/label.module';
import { DefaultCassandraOperationModule } from './operation/operation.module';
/* jhipster-needle-add-entity-module-import - JHipster will add entity modules imports here */

@NgModule({
    imports: [
        DefaultCassandraBankAccountModule,
        DefaultCassandraLabelModule,
        DefaultCassandraOperationModule,
        /* jhipster-needle-add-entity-module - JHipster will add entity modules here */
    ],
    declarations: [],
    entryComponents: [],
    providers: [],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class DefaultCassandraEntityModule {}
