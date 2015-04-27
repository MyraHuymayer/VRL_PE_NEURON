HocInterpreter = Transformator()
-- load custom lua script that serves as a replacement for ug_util
dofile("/Users/myra/NEURON-Projects/Parameter_Estimation/VRL-Plugin/VRL-PE_NEURON/src/main/resources/myfunctions.lua")

-----------------------------------------------------------------------
-- FUNCTIONS
-----------------------------------------------------------------------
	-------------------------------------------------------------------
	-- reduction of tables - this is only done for the model values 
	-- step_index indicates the next step chosen for analysis - i.e. step_index = 10 --> take every 10. step
	-------------------------------------------------------------------
	
	function reduceTimesteps(time, current, step_index)
		local timeModel_tmp2 = {}
		local kv4currentModel_tmp2 = {}
		for i = 1, #time, step_index do 
	
			timeModel_tmp2[#timeModel_tmp2 + 1] = time[i]
			kv4currentModel_tmp2[#kv4currentModel_tmp2 + 1] = current[i]
		
		end 
	
		return timeModel_tmp2, kv4currentModel_tmp2
	end
	
	-----------------------------------------------------------------------------------
	-- truncate data-values 
	-- time-values are represented as float with two decimal points 
	-- current values are represented in scientific notation with 6 decimal points
	-----------------------------------------------------------------------------------
	function cutDecimals(time, current)
		currentData = {}
		time2 = {}
		for i,v in ipairs(current) do
	
			currentData[#currentData + 1] =  string.format("%.6e",v) 
			time2[#time2 + 1] = string.format("%.2f", time[i])
			end
		return currentData, time2
	end
	-------------------------------------------------------------------
	-- change units  
	-- unit change for the model values =*10^9 --NOTE: model data are extremely small: femto ampere 
	-- unit change for exp values = *10^12
	-- this unit change would result in pico ampere for both data sets
	-------------------------------------------------------------------
	function convertUnits(data, exp)
		data_newunit = {}
		for i,v in ipairs(data) do 
			data_newunit[#data_newunit + 1] = v * 10^(exp)
		end 
		return data_newunit
	end 
	
	
	-------------------------------------------------------------------
	-- read from file 
	-------------------------------------------------------------------
	function readFromFile(filename,step)
		
		local file = io.open(filename,"r")
		time_file = {}
		kv4current_file = {}
		
		while true do
		        local first = file.read(file, "*n") 
		        time_file[step] = first
		        local second = file.read(file, "*n")
		        kv4current_file[step]  = second
		        if not second then break end
		        --print (line1, "  ", line2)
		        step = step +1
		        end
		       
		 return time_file, kv4current_file
	end 
	
	-------------------------------------------------------------------
	-- timespan, where non-linearity is observed in data 
	-------------------------------------------------------------------
	function clipData(tstart, tstop, time, current)
		local time_span = {}
		local current_span = {}
		
		for i,v in ipairs(time) do
			if tstart <= v and v <= tstop then 
				time_span[#time_span +1] = v
				current_span[#current_span + 1 ] = current[i]
			end 
		end 
		return time_span, current_span 
	end 
	
	-------------------------------------------------------------------
	-- the first line of the file is removed
	-------------------------------------------------------------------
	function removeFirstLine( filename )
	    local file = io.open( filename, "r" )
	    if file == nil then return nil end
	
	    content = {}
	
	    for line in file:lines() do
		
		    content[#content+1] = line
		end
		
	
	    file:close()
	    file = io.open( filename, "w+" )
		table.remove(content, 1)
		
	    for i = 1, #content do
		file:write( string.format( "%s\n", content[i] ) )
	    end
	
	    file:close()
	end

	-------------------------------------------------------------------
	-- append one table to another 
	-------------------------------------------------------------------
	function appendTables(time,current, newtime, newcurrent)
		for i = 1, #newtime do
			time[#time+1] = newtime[i]
			current[#current+1] = newcurrent[i]
		end 
		return time, current
	end 
-----------------------------------------------------------------
-- NEURON Plugin 
-----------------------------------------------------------------


-- hoc setup
--base_path = "path"
ug_path = --[##$$ PATHUG_String $$##]--
path = --[##$$ PATH_String $$##]--
base_path = GetParam("-base_path", path)

hoc_file = --[##$$ HOCFILE_String $$##]--
hoc_geom_ = GetParam("-hoc_geom", hoc_file)
hoc_geom = base_path .. hoc_geom_
print("Path to the hoc-File = "..hoc_geom)

hoc_dt = GetParamNumber("-hoc_dt", 2.5e-5)
hoc_tstop = GetParamNumber("-hoc_tstop", 2.69995)
hoc_finitialize = GetParamNumber("-hoc_finitialize", -40.0)


-----------------------------------------------------------------
-- Interface for parameter_estimation algorithm
-----------------------------------------------------------------
common_file_name= GetParam ("-common_file","") 
data_directory= GetParam ("-data_directory","") 
parameter_file_name = GetParam ("-parameter_file","")
		
-----------------------------------------------------------------
-- set variables relevant for the file naming conventions 
-----------------------------------------------------------------
vs = 99   
wolfe = 99
zoom = 99
ls_param = 99



--set the voltage step for the NEURON plugin
--set relevant hoc variables here 
--[##$$ VAR_NAME = VAR_Double $$##]--

--voltage_step is declared lateron

str = parameter_file_name

i1, _ = string.find(str, "min_" , 1)
i2, _ = string.find(str, "plus_" , 1)
i3, _ = string.find(str, "wolf" , 1)
i4, _ = string.find(str, "_z_" , 1)


if i1 ~= nil then
	_, _, ls_param = string.find(str, "min_(%d+)" , 1)
	_, _, vs = string.find(str, "param_(%d+)" , 1)

elseif i2 ~= nil then
	_, _, ls_param = string.find(str, "plus_(%d+)" , 1)
	_, _, vs = string.find(str, "param_(%d+)" , 1)
	
else 
	ls_param = 99 
	_, _, vs = string.find(str, "param_(%d+)" , 1)
end

if i3 ~= nil then
	_, _, wolfe, vs = string.find(str, "param_wolf(%d+)_(%d+)" , 1)
else wolfe = 99 end

if i4 ~= nil then 
	_, _, zoom = string.find(str, "_z_(%d+)" , 1)
else zoom = 99 end


-----------------------------------------------------------------
-- Set Default Parameters 
-----------------------------------------------------------------
--[##$$ PARAMETERS_HERE $$##]--


-----------------------------------------------------------------
-- Overwrite Default Parameters
-----------------------------------------------------------------
outputname = str
num = string.len(str) - 4


if parameter_file_name~="" then
	ug_load_script (data_directory..parameter_file_name)
	outputname = string.sub (str, 12, num)
	end
				
		
-- create neuron instance (java PE <-> ugshell -> NEURON -> mod file wird geladen)
HocInterpreter = Transformator()
--hoc_step = 1
	for hoc_step=0, 1, 1  do 
		-- load geometry and stimulation protocol hoc files
		HocInterpreter:load_geom(hoc_geom)
		
	
		
		-----------------------------------------------------------------
		-- Reset Parameters "in hoc-File" 
		-----------------------------------------------------------------
		--[##$$ SET_PARAMETERS_HERE $$##]--
		--e.g.: HocInterpreter:execute_hoc_stmt("a_new ="..a_new.."")

		if hoc_step == 1 then 
			--set the variables for the parameter (ls_param), the wolfe step, the voltage_step and the zoom step
			HocInterpreter:execute_hoc_stmt("voltage_step ="..vs.."")
			HocInterpreter:execute_hoc_stmt("ls_param ="..ls_param.."")
			HocInterpreter:execute_hoc_stmt("wolf ="..wolfe.."")
			HocInterpreter:execute_hoc_stmt("zoom ="..zoom.."")
			--[##$$ SET_VARIABLES_HERE $$##]--
			
			end 
			
		HocInterpreter:execute_hoc_stmt("init_cell(--[##$$ PARAMETERNAMES_HERE $$##]--)")
		HocInterpreter:execute_hoc_stmt("finitialize()")
		
		HocInterpreter:execute_hoc_stmt("run()")
		HocInterpreter:execute_hoc_stmt("writefile()")
		

end 


-----------------------------------------------------------------
-- Provide Model Solutions
-----------------------------------------------------------------



filename_Model = base_path.."--[##$$ MODEL_FILENAME_PART1 $$##]--_"..wolfe.."_"..vs.."_"..zoom.."_--[##$$ MODEL_FILENAME_PART2 $#]--_"..ls_param..".txt"
removeFirstLine( filename_Model )
timeModel, currentModel = readFromFile(filename_Model, 1)

--choose relevant time-spans

--[##$$ FUNC_clipData()_M FUNC_reduceTimesteps()_M  FUNC_appendTables() $$##]-- 


--[##$$ FUNC_convertUnits()_M $$##]-- 


-- das folgende muss wohl nicht geaendert werden 
final_currentModel, final_time_Model = cutDecimals(timeModel, currentModel)

-----------------------------------------------------------------
-- Provide Reference Solution --> experimental data
-----------------------------------------------------------------

filename_expData  = --[##$$ filename $$##]-- 

timeData, currentData = readFromFile(filename_expData,1)

-- convert units of exp. data (e.g.: s --> ms)
--[##$$ FUNC_convertUnits_ED $$##]-- 




final_currentData, final_time_expData = cutDecimals(timeData, currentData)	


-----------------------------------------------------------------
-- prepare variable for total defect
-----------------------------------------------------------------
l2_defect = 0.0

-----------------------------------------------------------------
-- Evaluate Residia and write to common file
-----------------------------------------------------------------
if common_file_name ~="" then
	residua = {}
	for i, v in ipairs(final_time_expData) do 
		for j, w in ipairs(final_time_Model) do
			
			if final_time_expData[i] == final_time_Model[j]  then
				
				res_i = final_currentModel[j] - final_currentData[i] 
				
				local file = io.open(data_directory..common_file_name,"a")
				
				l2_defect = l2_defect + res_i * res_i
				file:write(res_i)
				file:write(" ")
				--print("L2-defect = ", l2_defect)
				residua[#residua + 1] = final_currentModel[j] - final_currentData[i] 
				
				file:close() 

			end
			
		end
	end
	

end

	-----------------------------------------------------------------
	-- End of evaluation
	-----------------------------------------------------------------

if common_file_name~="" then
	local file = io.open(data_directory..common_file_name,"a")
	file:write(l2_defect)
	file:write(" ")
	print("L2-defect: ", l2_defect)
	file:close()

end	

--os.remove(filename_Model)
--fn_dummy_step = base_path.."--[##$$ MODEL_FILENAME_PART1 $$##]--_0_0_0_--[##$$ MODEL_FILENAME_PART2 $#]--_99.txt"
--os.remove(fn_dummy_step)
--fn_dummy_step = base_path.."--[##$$ MODEL_FILENAME_PART1 $$##]--_0_"..vs.."_0_--[##$$ MODEL_FILENAME_PART2 $#]--_99.txt" 
--os.remove(fn_dummy_step) 