import { NgModule } from '@angular/core';
import { Title } from '@angular/platform-browser';

import {
    DefaultCassandraSharedLibsModule,
    JhiAlertComponent,
    JhiAlertErrorComponent
} from './';

@NgModule({
    imports: [
        DefaultCassandraSharedLibsModule
    ],
    declarations: [
        JhiAlertComponent,
        JhiAlertErrorComponent
    ],
    providers: [
        Title
    ],
    exports: [
        DefaultCassandraSharedLibsModule,
        JhiAlertComponent,
        JhiAlertErrorComponent
    ]
})
export class DefaultCassandraSharedCommonModule {}
