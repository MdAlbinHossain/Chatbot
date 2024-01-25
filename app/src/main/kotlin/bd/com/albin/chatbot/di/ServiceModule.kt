package bd.com.albin.chatbot.di

import bd.com.albin.chatbot.data.service.AccountService
import bd.com.albin.chatbot.data.service.ConfigurationService
import bd.com.albin.chatbot.data.service.LogService
import bd.com.albin.chatbot.data.service.StorageService
import bd.com.albin.chatbot.data.service.impl.AccountServiceImpl
import bd.com.albin.chatbot.data.service.impl.ConfigurationServiceImpl
import bd.com.albin.chatbot.data.service.impl.LogServiceImpl
import bd.com.albin.chatbot.data.service.impl.StorageServiceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ServiceModule {
    @Binds
    abstract fun provideAccountService(impl: AccountServiceImpl): AccountService

    @Binds abstract fun provideLogService(impl: LogServiceImpl): LogService

    @Binds abstract fun provideStorageService(impl: StorageServiceImpl): StorageService

    @Binds
    abstract fun provideConfigurationService(impl: ConfigurationServiceImpl): ConfigurationService
}