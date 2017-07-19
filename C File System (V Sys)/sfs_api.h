#ifndef _INCLUDE_SFS_API_H_
#define _INCLUDE_SFS_API_H_
#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <libgen.h>
#define fileNameConstraint 60
#define disk "sfs_disk.disk"
#define blockSize 1024
#define numberOfBlocks 200 
#define numberINodes 20 
#define unused 1
#define used 0
#define maxOpenedFiles 32
#define blockPointerSize 4
#define startOfBitMap 1
#define totalBitMapBlocks (sizeof(BitMap)/blockSize + 1)
#define iNodeBlockStart totalBitMapBlocks + 1
#define numberINodeBlocks (sizeof(InodeTable)/blockSize + 1)
#define directoryBlockStart 1 + numberINodeBlocks + totalBitMapBlocks
#define numberOfDirectoryBlocks (sizeof(DirectoryEntryTable)/blockSize + 1)
#define dataBlockStart 1 + numberINodeBlocks + totalBitMapBlocks + numberOfDirectoryBlocks
#define maxFileSize blockSize*(12 + blockSize/blockPointerSize)

typedef struct {

    uint64_t magic;
    uint64_t block_size;
    uint64_t fs_size;
    uint64_t inode_table_len;
    uint64_t root_dir_inode;
} superBlock;

typedef struct {

    unsigned int size;
    int direct[12];
    int indirectPointer;
} inode_t;


typedef struct {

    int status;
    uint64_t inode;
    uint64_t readptr;
    uint64_t writeptr;
} fd;

typedef struct
{
    uint16_t byte_address;
} FilePtr;

typedef struct directory_entry_t {

    int inodeNum;
    char fileName[20];
    int status;
} DirectoryEntry;

typedef struct inode_table{

  inode_t table[numberINodes];

}InodeTable;

typedef struct bitMap{

  int blocks[numberOfBlocks];
  int inodeIndexes[numberINodes];
  int nextFreeBlock;
  int nextFreeInode;

} BitMap;

typedef struct directory_entry_table{
  DirectoryEntry entries[numberINodes];

} DirectoryEntryTable;

void mksfs(int fresh); //void mksfs(int fresh);
int sfs_get_next_file_name(char *fname); //int sfs_getnextfilename(char *fname);
int sfs_get_file_size(char* path); //int sfs_getfilesize(const char* path);
int sfs_fopen(char *name); //int sfs_fopen(char *name);
int sfs_fclose(int fileID); //int sfs_fclose(int fileID);
int sfs_frseek(int fileID, int loc); //int sfs_fseek(int fileID, int loc);
int sfs_fwseek(int fileID, int loc);
int sfs_fwrite(int fileID, char *buf, int length); //int sfs_fwrite(int fileID, const char *buf, int length);
int sfs_fread(int fileID, char *buf, int length); //int sfs_fread(int fileID, char *buf, int length);
int sfs_remove(char *file); //int sfs_remove(char *file);

#endif
