
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Random;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author shannon
 */
public class CreateGraphFile {
    
    public static void main(String args[])
    {
     try
        {
            FileWriter writer = new FileWriter(args[0]);
            BufferedWriter buffer = new BufferedWriter(writer);
            
            Random r = new Random(System.currentTimeMillis());
        
         
            int size = Integer.parseInt(args[1]);
            
            //loop through all but the 0 and last, which are our added source and sink
            
            for(int i =1; i< size;i++)
            {
                buffer.write(0 + " ");
                
                for(int j =1; j< size;j++)
                {
                    buffer.write(r.nextInt(500) + " ");   
                }
                buffer.write("\n");   
            }
            
            //write last row for sink
             for(int j =1; j< size+1;j++)
            {
               buffer.write(0 + " ");
            }
             
            buffer.close();
        }
        catch(Exception e)
        {
            System.out.println("An error occurred " + e.getMessage());
            
        }
    }

}
