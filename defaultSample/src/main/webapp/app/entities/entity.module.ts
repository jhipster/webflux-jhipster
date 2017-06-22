import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';

import { DefaultSampleBankAccountModule } from './bank-account/bank-account.module';
import { DefaultSampleLabelModule } from './label/label.module';
import { DefaultSampleOperationModule } from './operation/operation.module';
/* jhipster-needle-add-entity-module-import - JHipster will add entity modules imports here */

@NgModule({
    imports: [
        DefaultSampleBankAccountModule,
        DefaultSampleLabelModule,
        DefaultSampleOperationModule,
        /* jhipster-needle-add-entity-module - JHipster will add entity modules here */
    ],
    declarations: [],
    entryComponents: [],
    providers: [],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class DefaultSampleEntityModule {}
