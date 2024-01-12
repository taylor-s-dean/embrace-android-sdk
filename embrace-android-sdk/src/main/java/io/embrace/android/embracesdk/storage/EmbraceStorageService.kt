package io.embrace.android.embracesdk.storage

import android.content.Context
import java.io.File
import java.io.FilenameFilter

/**
 * Provides File instances for files and directories used to store data.
 * Previous versions of the SDK used the cache directory for cached files.
 * Since v6.3.0, the files directory is used instead and the cache directory is only used for
 * cached config files.
 */
internal class EmbraceStorageService(
    private val context: Context
) : StorageService {

    private val cacheDirectory: File by lazy {
        context.cacheDir
    }

    private val filesDirectory: File by lazy {
        getOrCreateEmbraceFilesDir() ?: cacheDirectory
    }

    override fun getFileForRead(name: String): File {
        val fileInFilesDir = File(filesDirectory, name)

        if (!fileInFilesDir.exists()) {
            val fileInCacheDir = File(cacheDirectory, name)
            if (fileInCacheDir.exists()) {
                return fileInCacheDir
            }
        }
        return fileInFilesDir
    }

    override fun getFileForWrite(name: String): File {
        return File(filesDirectory, name)
    }

    override fun getConfigCacheDir(): File {
        return File(cacheDirectory, EMBRACE_CONFIG_CACHE_DIRECTORY)
    }

    override fun getNativeCrashDir(): File {
        return File(filesDirectory, NATIVE_CRASH_FILE_FOLDER)
    }

    override fun listFiles(filter: FilenameFilter): List<File> {
        val filesDir = filesDirectory.listFiles(filter) ?: emptyArray()
        val cacheDir = cacheDirectory.listFiles(filter) ?: emptyArray()
        return filesDir.toList() + cacheDir.toList()
    }

    /**
     * Get or create the Embrace folder inside the files directory.
     */
    private fun getOrCreateEmbraceFilesDir(): File? {
        val filesDir = File(context.filesDir, EMBRACE_DIRECTORY)
        return try {
            filesDir.mkdirs()
            filesDir.takeIf { it.exists() }
        } catch (e: SecurityException) {
            null
        }
    }
}

/**
 * Directory name for files that are stored in the files directory.
 */
private const val EMBRACE_DIRECTORY = "embrace"

/**
 * Directory name for the config files that are stored in the cache directory.
 */
private const val EMBRACE_CONFIG_CACHE_DIRECTORY = "emb_config_cache"

/**
 * Directory name for the native crash files that are stored in the files directory.
 */
internal const val NATIVE_CRASH_FILE_FOLDER = "ndk"