package nz.eloque.foss_wallet.persistence.pass

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import nz.eloque.foss_wallet.model.OriginalPass
import nz.eloque.foss_wallet.model.Pass
import nz.eloque.foss_wallet.model.PassGroup
import nz.eloque.foss_wallet.model.PassWithLocalization
import nz.eloque.foss_wallet.persistence.PassBitmaps

class PassRepository @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val passDao: PassDao
) {

    fun all(): Flow<List<PassWithLocalization>> = passDao.all()

    fun updatable(): List<Pass> = passDao.updatable()

    fun filtered(query: String): Flow<List<PassWithLocalization>> {
        return if (query.isEmpty()) {
            all()
        } else {
            val result = all()
            result.map { it.filter { it.pass.contains(query) } } }
    }

    fun byId(id: String): PassWithLocalization = passDao.byId(id)

    fun associate(pass: Pass, group: PassGroup) = passDao.associate(pass.id, group.id)

    fun insert(pass: Pass, bitmaps: PassBitmaps, originalPass: OriginalPass) {
        val id = pass.id
        passDao.insert(pass)
        bitmaps.saveToDisk(context, id)
        originalPass.saveToDisk(context, id)
    }

    fun insert(group: PassGroup): PassGroup {
        val id = passDao.insert(group)
        return group.copy(id)
    }

    fun delete(pass: Pass) {
        pass.deleteFiles(context)
        passDao.delete(pass)
    }

    fun dessociate(pass: Pass, groupId: Long) = passDao.dessociate(pass, groupId)

    fun deleteGroup(groupId: Long) = passDao.delete(PassGroup(groupId))
    fun associate(groupId: Long, passes: Set<Pass>) = passDao.associate(groupId, passes)
}
