-- load ug script util
path_to_ug = "/Users/myra/Documents/workspace/ug4" --[##$$ PATHUG $#]
ug_load_script(path_to_ug.."/scripts/ug_util.lua") --path not correct!!!
ug_load_script(path_to_ug.."/scripts/util/gnuplot.lua") --path not correct!!!


-- script name 
scriptname = debug.getinfo(1).short_src
print("Executing script: " .. scriptname)


-- check if plugins are loaded for NEURON 
AssertPluginsLoaded( {"MembranePotentialMapping"} )

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
path = --[##$$ PATH_String $$##]--
base_path = util.GetParam("-base_path", path, "Base path to project")

hoc_file = --[##$$ HOCFILE_String $$##]--
hoc_geom_ = util.GetParam("-hoc_geom", hoc_file)
hoc_geom = base_path .. hoc_geom_

hoc_dt = util.GetParamNumber("-hoc_dt", 2.5e-5)
hoc_tstop = util.GetParamNumber("-hoc_tstop", 2.69995)
hoc_finitialize = util.GetParamNumber("-hoc_finitialize", -40.0)


-----------------------------------------------------------------
-- Interface for parameter_estimation algorithm
-----------------------------------------------------------------
common_file_name= util.GetParam ("-common_file","") 
data_directory= util.GetParam ("-data_directory","") 
parameter_file_name = util.GetParam ("-parameter_file","")
		
-----------------------------------------------------------------
-- set variables relevant for the file naming conventions 
-----------------------------------------------------------------
vs = 1   
wolfe = 0
zoom = 0
ls_param = 0



--set the voltage step for the NEURON plugin
--set relevant hoc variables here 
--[##$$ VAR_NAME = VAR_Double $$##]--

--voltage_step is declared lateron

str = parameter_file_name

i1, j1 = string.find(str, "min_" , 12)
i2, j2 =  string.find(str, "plus_" , 12)
i3, j3 = string.find(str, "wolf_" , 12)
i4, j4 = string.find(str, "_z_" , 12)

print("i3 = ",i3)
print("parameter file name = ", str)
num1 = string.len(str) - 5
num2 = string.len(str) - 4

-- set the variable for the parameter 
if i1 == nil and j1 == nil and  i2 == nil and j2 == nil then 
	ls_param = 99
	
elseif i1 ~= nil and j1 ~= nil then
	local _start = j1 + 1
	local _end = j1 + 2
	ls_param = tonumber(string.sub(str, _start , _end ))
	
	--if only one digit
	if ls_param == nil then 
		ls_param = tonumber(string.sub(str, _start , _start ))
		end 
	
	vs = 2
	
elseif i2 ~= nil and j2 ~= nil then
	 local _start = j2 + 1
	 local _end = j2 + 2
	 
	 ls_param = tonumber(string.sub(str, _start , _end ))
	--if only one digit
	if ls_param == nil then 
		ls_param = tonumber(string.sub(str, _start , _start ))
		end
		 
	vs = 3
end  

--set the variable wolfe
if i3 ~= nil and j3 ~= nil then 
	local _start = j3 + 1
	local _end = j3 + 2
	
	wolfe = tonumber(string.sub(str, _start , _end ))
	
	if wolfe == nil then 
		wolfe = tonumber(string.sub(str, _start , _start))
	end 
	
end 

if i4 ~= nil and j4 ~= nil then 
	local _start = j4 + 1
	local _end = j4 + 2
	
	zoom = tonumber(string.sub(str, _start , _end ))
	
	if zoom == nil then 
		zoom = tonumber(string.sub(str, _start , _start))
	end 
	
end 

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
				
print("kic_new = ", kic_new)	
		
print(" parameter_file  = " .. parameter_file_name)
		
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

		if vs ~= 1 then 
			HocInterpreter:execute_hoc_stmt("voltage_step ="..vs.."")
		else 
			HocInterpreter:execute_hoc_stmt("voltage_step ="..hoc_step.."")
		end 
		
		
		print("Voltage-step = ", HocInterpreter:get_hoc_variable("voltage_step"))
		if hoc_step == 1 then 
			--set the variables for the parameter (ls_param), the wolfe step and the zoom step
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

--"voltage_step" should match between model text file name (Fig1c_kv4_voltageClamp_P1_1.txt) and exp. data text file name (Trace_1_9_1_1.txt)
voltage_step = vs 


filename_Model = base_path.."--[##$$ MODEL_FILENAME_PART1 $$##]--_"..wolfe..""..voltage_step..""..zoom.."_--[##$$ MODEL_FILENAME_PART2 $#]--_"..ls_param..".txt"
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

filename_expData  = base_path..--[##$$ filename $$##]-- 

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

os.remove(filename_Model)
fn_dummy_step = base_path.."--[##$$ MODEL_FILENAME_PART1 $$##]--_000_--[##$$ MODEL_FILENAME_PART2 $#]--_99.txt"
os.remove(fn_dummy_step)
fn_dummy_step = base_path.."--[##$$ MODEL_FILENAME_PART1 $$##]--_0"..voltage_step.."0_--[##$$ MODEL_FILENAME_PART2 $#]--_99.txt" 
os.remove(fn_dummy_step) 