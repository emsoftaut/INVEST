package _parametricverify;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import param.FunctionFactory;
import param.ModelBuilder;
import param.ParamModel;
import param.ParamResult;
import parser.ast.ModulesFile;
import parser.ast.PropertiesFile;
import prism.Prism;
import prism.PrismException;
import prism.PrismFileLog;
import prism.PrismLangException;
import prism.PrismLog;
import prismv.PrismModel;
import prismv.PrismProperty;
import prismv.PrismResult;
import simulator.ModulesFileModelGenerator;

public class ParametricTestClient {


///prism seqgen.pm seq.pctl -param  'p1=0.0:1.0,p2=0.0:1.0,c1=0.0:1.0,c2=0.0:1.0'
	public static String readFileAsString(String filename) throws IOException {
		return Files.readString(Path.of(filename));
	}


	public static void main(String[] args) 
	{

		Prism prism = null;
		PrismLog prismMainLog;
		prismMainLog = new PrismFileLog("prismLogFile.txt");		
		prism = new Prism(prismMainLog);

		String fileContents = "dtmc\n"
				+ "const double p1;\n"
				+ "const double p2;\n"
				+ "module simple\n"
				+ "    state : [0..3] init 0;\n"
				+ "    [action1] (state=0) -> p1:(state'=1) + (1-p1):(state'=3);\n"
				+ "    [action2] (state=1) -> p2:(state'=2) + (1-p2):(state'=3);\n"
				+ "    [fail] (state=3) -> true;\n"
				+ "endmodule\n";

		try {				
			prism.initialise();
			String[] paramNames = new String[]{"p1","p2"};
			String[] paramLowerBounds = new String[]{"0","0"};
			String[] paramUpperBounds = new String[]{"1","1"};

			ModulesFile modulesFile = prism.parseModelString(fileContents);
			prism.loadPRISMModel(modulesFile);
			prism.setPRISMModelConstants(null, true);
			PropertiesFile propertiesFile = prism.parsePropertiesString(modulesFile,"P=?[F(state=3)]");	

			prism.Result fromPrismResult = prism.modelCheckParametric(propertiesFile,propertiesFile.getPropertyObject(0), 
					paramNames, paramLowerBounds, paramUpperBounds);

			System.out.println(fromPrismResult);
			ParamResult r =  (ParamResult) fromPrismResult.getResult();
			System.out.println(r);
		} catch (PrismException e) 
		{				
			e.printStackTrace();
		}
	}

}
