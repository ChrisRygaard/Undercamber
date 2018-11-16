package com.rygaardtechnologies.utilities;

final public class UndercamberJARMaker
{
   final static java.io.File UNDERCAMBER_ROOT_DIRECTORY = new java.io.File( System.getenv("UNDERCAMBER_PROJECT_ROOT") );

   final static String       ENTRY_CLASS   = "com.undercamber.Undercamber";
   final static java.io.File JAR_FILE      = getFile( "/Undercamber.jar" );
   final static String       DIRECTORIES[] = { "/Source" };

   UndercamberJARMaker()
      throws java.io.IOException
   {
      byte         buffer[];
      int          index;
      java.io.File classDirectories[];

      buffer = new byte[ 262144 ];

      classDirectories = new java.io.File[ DIRECTORIES.length ];
      for ( index=0; index<DIRECTORIES.length; index++ )
      {
         classDirectories[ index ] = getFile( DIRECTORIES[index] );
      }

      JAR_FILE.getParentFile().mkdirs();

      try ( java.io.FileOutputStream fileOutputStream = new java.io.FileOutputStream(JAR_FILE) )
      {
         try ( java.io.BufferedOutputStream bufferedOutputStream = new java.io.BufferedOutputStream(fileOutputStream,262144) )
         {
            try ( java.util.zip.ZipOutputStream zipOutputStream = new java.util.zip.ZipOutputStream(bufferedOutputStream) )
            {
               for ( java.io.File classDirectory : classDirectories )
               {
                  insertDirectory( zipOutputStream,
                                   classDirectory,
                                   buffer,
                                   ".java" );
               }
            }
         }
      }
   }

   final static java.io.File getFile( String relativePath )
   {
      String platformSpecificRelativePath;

      platformSpecificRelativePath = relativePath.replace( '/', java.io.File.separatorChar );

      return new java.io.File( UNDERCAMBER_ROOT_DIRECTORY, platformSpecificRelativePath );
   }

   final void insertDirectory( java.util.zip.ZipOutputStream zipOutputStream,
                               java.io.File                  directory,
                               byte                          buffer[],
                               String...                     excludedSuffixes )
      throws java.io.IOException
   {
      for ( java.io.File file : directory.listFiles() )
      {
         insertFiles( zipOutputStream,
                      "",
                      file,
                      excludedSuffixes,
                      new String[0],
                      buffer );
      }
   }

   final private void insertFiles( java.util.zip.ZipOutputStream zipOutputStream,
                                   String                        zipDirectoryName,
                                   java.io.File                  file,
                                   String                        excludedSuffixes[],
                                   String                        excludedFiles[],
                                   byte                          buffer[] )
      throws java.io.IOException
   {
      String                 subdirectoryName;
      java.io.File           subdirectoryFiles[];
      String                 zipEntryName;
      java.util.zip.ZipEntry zipEntry;
      int                    numberOfBytesRead;

      if ( isIncluded(file,excludedFiles) )
      {
         if ( file.isDirectory() )
         {
            if ( zipDirectoryName.length() == 0 )
            {
               subdirectoryName = getUnqualifiedFileName( file );
            }
            else
            {
               subdirectoryName = zipDirectoryName + "/" + getUnqualifiedFileName( file );
            }

            subdirectoryFiles = file.listFiles();

            java.util.Arrays.sort( subdirectoryFiles );

            for ( java.io.File subdirectoryFile : subdirectoryFiles )
            {
               insertFiles( zipOutputStream,
                            subdirectoryName,
                            subdirectoryFile,
                            excludedSuffixes,
                            excludedFiles,
                            buffer );
            }
         }
         else
         {
            if ( isAllowed(file,
                           excludedSuffixes) )
            {
               if ( zipDirectoryName.length() == 0 )
               {
                  zipEntryName = getUnqualifiedFileName( file );
               }
               else
               {
                  zipEntryName = zipDirectoryName + "/" + getUnqualifiedFileName( file );
               }

               zipEntry = new java.util.zip.ZipEntry( zipEntryName );

               zipOutputStream.putNextEntry( zipEntry );

               try ( java.io.FileInputStream fileInputStream = new java.io.FileInputStream(file) )
               {
                  numberOfBytesRead = fileInputStream.read( buffer );

                  while ( numberOfBytesRead != -1 )
                  {
                     zipOutputStream.write( buffer,
                                            0,
                                            numberOfBytesRead );

                     numberOfBytesRead = fileInputStream.read( buffer );
                  }
               }

               zipOutputStream.closeEntry();
            }
         }
      }
   }

   final private boolean isIncluded( java.io.File file,
                                     String       excludedFiles[] )
   {
      String path;

      path = file.getPath();

      for ( String excludedFile : excludedFiles )
      {
         if ( path.equals(excludedFile) )
         {
            return false;
         }
      }

      return true;
   }

   final private boolean isAllowed( java.io.File file,
                                    String       excludedSuffixes[] )
   {
      String fileName;

      fileName = file.getName();

      for ( String excludedSuffix : excludedSuffixes )
      {
         if ( fileName.endsWith(excludedSuffix) )
         {
            return false;
         }
      }

      return true;
   }

   final static String getUnqualifiedFileName( java.io.File file )
   {
      String fileName;
      int    separatorIndex;

      fileName = file.getName();

      separatorIndex = fileName.lastIndexOf( java.io.File.separator );
      if ( separatorIndex >= 0 )
      {
         fileName = fileName.substring( separatorIndex + 1 );
      }

      return fileName;
   }

   final public static void main( String arguments[] )
   {
      try
      {
         new UndercamberJARMaker();
      }
      catch ( java.io.IOException ioException )
      {
         ioException.printStackTrace();
      }
   }
}
