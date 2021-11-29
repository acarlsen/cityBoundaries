package dk.tacit.android.cityboundaries.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dk.tacit.android.cityboundaries.common.Constants
import dk.tacit.android.cityboundaries.data.location.AppLocationManagerImpl
import dk.tacit.android.cityboundaries.data.remote.CitiesApi
import dk.tacit.android.cityboundaries.data.repository.CitiesRepositoryImpl
import dk.tacit.android.cityboundaries.domain.location.AppLocationManager
import dk.tacit.android.cityboundaries.domain.repository.CitiesRepository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideCitiesApi(): CitiesApi {
        return Retrofit.Builder()
            .baseUrl(Constants.CITIES_API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CitiesApi::class.java)
    }

    @Provides
    @Singleton
    fun provideCitiesRepository(api: CitiesApi): CitiesRepository {
        return CitiesRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun provideLocationManager(@ApplicationContext appContext: Context): AppLocationManager {
        return AppLocationManagerImpl(appContext)
    }
}