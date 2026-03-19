package com.example.test.mainbiz.util

import java.io.File
import java.nio.file.Files
import java.nio.file.attribute.PosixFilePermission

object FilePermissionUtil {
    fun setPermissions(file: File, permissionStr: String, recursive: Boolean = true) {
        val path = file.toPath()
        if (Files.isSymbolicLink(path) && !Files.exists(path)) {
            return
        }
        val permissions = stringsToPosixFilePermissions(permissionStr)
        Files.setPosixFilePermissions(file.toPath(), permissions)

        if (recursive && file.isDirectory) {
            setPermissionsRecursive(file, permissions)
        }
    }

    private fun stringsToPosixFilePermissions(permissionStr: String): Set<PosixFilePermission> {
        val owner = permissionStr.take(3)
        val group = permissionStr.drop(3).take(3)
        val others = permissionStr.drop(6).take(3)

        val result = mutableSetOf<PosixFilePermission>()
        if (owner[0] == 'r') result.add(PosixFilePermission.OWNER_READ)
        if (owner[1] == 'w') result.add(PosixFilePermission.OWNER_WRITE)
        if (owner[2] == 'x') result.add(PosixFilePermission.OWNER_EXECUTE)
        if (group[0] == 'r') result.add(PosixFilePermission.GROUP_READ)
        if (group[1] == 'w') result.add(PosixFilePermission.GROUP_WRITE)
        if (group[2] == 'x') result.add(PosixFilePermission.GROUP_EXECUTE)
        if (others[0] == 'r') result.add(PosixFilePermission.OTHERS_READ)
        if (others[1] == 'w') result.add(PosixFilePermission.OTHERS_WRITE)
        if (others[2] == 'x') result.add(PosixFilePermission.OTHERS_EXECUTE)
        return result
    }

    private fun setPermissionsRecursive(dir: File, permissions: Set<PosixFilePermission>) {
        dir.listFiles()?.forEach { child ->
            val childPath = child.toPath()
            if (!Files.isSymbolicLink(childPath) && Files.exists(childPath)) {
                Files.setPosixFilePermissions(childPath, permissions)
            }
            if (child.isDirectory) {
                setPermissionsRecursive(child, permissions)
            }
        }
    }
}
